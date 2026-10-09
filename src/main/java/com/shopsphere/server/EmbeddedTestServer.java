package com.shopsphere.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Lightweight local HTTP test server used during CI runs to avoid
 * third-party rate limiting or bot-challenge interruptions.
 */
public class EmbeddedTestServer {

    private static HttpServer server;
    private static int port = 8080;
    private static volatile boolean running = false;

    public static synchronized int startDynamic() {
        if (running) return port;
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", new HomeHandler());
            server.createContext("/search", new SearchHandler());
            server.createContext("/apple-macbook-pro", new ProductHandler("Apple MacBook Pro 13-inch"));
            server.createContext("/cart", new CartHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/register", new RegisterHandler());
            server.createContext("/computers", new CategoryHandler("Computers"));
            server.createContext("/electronics", new CategoryHandler("Electronics"));
            server.createContext("/apparel", new CategoryHandler("Apparel"));
            server.createContext("/digital-downloads", new CategoryHandler("Digital downloads"));
            server.createContext("/books", new CategoryHandler("Books"));
            server.createContext("/jewelry", new CategoryHandler("Jewelry"));
            server.createContext("/gift-cards", new CategoryHandler("Gift Cards"));
            server.createContext("/sitemap", new InfoPageHandler("Sitemap"));
            server.createContext("/shipping-returns", new InfoPageHandler("Shipping & returns"));
            server.createContext("/privacy-notice", new InfoPageHandler("Privacy notice"));
            server.createContext("/conditions-of-use", new InfoPageHandler("Conditions of use"));
            server.createContext("/contactus", new ContactUsHandler());
            server.createContext("/passwordrecovery", new PasswordRecoveryHandler());
            server.createContext("/wishlist", new WishlistHandler());
            server.setExecutor(null);
            server.start();
            port = server.getAddress().getPort();
            running = true;
            System.out.println("[EmbeddedTestServer] Hermetic test server running on http://127.0.0.1:" + port + "/");
            return port;
        } catch (IOException e) {
            System.err.println("[EmbeddedTestServer] Failed to start server: " + e.getMessage());
            return 8080;
        }
    }

    public static synchronized void stop() {
        if (server != null && running) {
            server.stop(0);
            running = false;
            System.out.println("[EmbeddedTestServer] Stopped.");
        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static int getPort() {
        return port;
    }

    private static void sendHtml(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String getCommonHeader() {
        return """
            <header class="header">
                <div class="header-logo"><a href="/"><img alt="nopCommerce demo store" src="/logo.png" /></a></div>
                <div class="search-box">
                    <form action="/search" method="get" onsubmit="var val=document.getElementById('small-searchterms').value.trim();if(!val){alert('Please enter some search keyword');return false;}">
                        <input type="text" class="search-box-text" id="small-searchterms" autocomplete="off" name="q" placeholder="Search store" />
                        <button type="submit" class="button-1 search-box-button">Search</button>
                    </form>
                </div>
                <div class="header-links-wrapper">
                    <div class="header-links">
                        <ul>
                            <li><a href="/register" class="ico-register">Register</a></li>
                            <li><a href="/login" class="ico-login">Log in</a></li>
                            <li><a href="/wishlist" class="ico-wishlist"><span class="wishlist-label">Wishlist</span> <span class="wishlist-qty">(0)</span></a></li>
                            <li><a href="/cart" class="ico-cart"><span class="cart-label">Shopping cart</span> <span class="cart-qty">(0)</span></a></li>
                        </ul>
                    </div>
                </div>
                <div class="currency-selector">
                    <select id="customerCurrency" name="customerCurrency" onchange="var p=document.querySelectorAll('.product-item .actual-price, .product-price span');var isEuro=this.value==='EUR'||this.options[this.selectedIndex].text.indexOf('Euro')!==-1;p.forEach(function(el){el.textContent=isEuro?'€1,500.00':'$1,800.00';});">
                        <option selected="selected" value="USD">US Dollar</option>
                        <option value="EUR">Euro</option>
                    </select>
                </div>
                <div class="header-menu">
                    <ul class="top-menu notmobile">
                        <li><a href="/computers">Computers</a></li>
                        <li><a href="/electronics">Electronics</a></li>
                        <li><a href="/apparel">Apparel</a></li>
                        <li><a href="/digital-downloads">Digital downloads</a></li>
                        <li><a href="/books">Books</a></li>
                        <li><a href="/jewelry">Jewelry</a></li>
                        <li><a href="/gift-cards">Gift Cards</a></li>
                    </ul>
                </div>
                <div id="bar-notification" class="bar-notification" style="display:none;">
                    <p class="content"></p>
                    <span class="close" title="Close" onclick="document.getElementById('bar-notification').style.display='none';">&nbsp;</span>
                </div>
                <script>
                function showBarNotification(msg) {
                    var bar = document.getElementById('bar-notification');
                    if (bar) {
                        var content = bar.querySelector('.content') || bar.querySelector('p');
                        if (content) content.textContent = msg;
                        bar.className = 'bar-notification success';
                        bar.style.display = 'block';
                    }
                }
                function syncHeaderBadges() {
                    try {
                        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                        var cQty = cart.reduce(function(a,b){ return a + (b.qty || 0); }, 0);
                        var cEl = document.querySelector('.ico-cart .cart-qty');
                        if (cEl) cEl.textContent = '(' + cQty + ')';

                        var wish = JSON.parse(localStorage.getItem('wishlist') || '[]');
                        var wQty = wish.reduce(function(a,b){ return a + (b.qty || 0); }, 0);
                        var wEl = document.querySelector('.ico-wishlist .wishlist-qty');
                        if (wEl) wEl.textContent = '(' + wQty + ')';
                    } catch(e) {}
                }
                function addToCart(id, name, price) {
                    var qtyInput = document.getElementById('product_enteredQuantity_' + id);
                    var qty = qtyInput ? parseInt(qtyInput.value) || 1 : 1;
                    var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                    var found = cart.find(function(item) { return item.id === id; });
                    if (found) {
                        found.qty += qty;
                    } else {
                        cart.push({ id: id, name: name, price: price, qty: qty });
                    }
                    localStorage.setItem('cart', JSON.stringify(cart));
                    syncHeaderBadges();
                    showBarNotification('The product has been added to your shopping cart');
                }
                function addToWishlist(id, name, price) {
                    var wish = JSON.parse(localStorage.getItem('wishlist') || '[]');
                    var found = wish.find(function(item) { return item.id === id; });
                    if (found) {
                        found.qty += 1;
                    } else {
                        wish.push({ id: id, name: name, price: price, qty: 1 });
                    }
                    localStorage.setItem('wishlist', JSON.stringify(wish));
                    syncHeaderBadges();
                    showBarNotification('The product has been added to your wishlist');
                }
                syncHeaderBadges();
                document.addEventListener('DOMContentLoaded', syncHeaderBadges);
                </script>
            </header>
            """;
    }

    private static String getCommonFooter() {
        return """
            <footer class="footer">
                <div class="footer-upper">
                    <div class="footer-block information">
                        <div class="title"><strong>Information</strong></div>
                        <ul class="list">
                            <li><a href="/sitemap">Sitemap</a></li>
                            <li><a href="/shipping-returns">Shipping &amp; returns</a></li>
                            <li><a href="/privacy-notice">Privacy notice</a></li>
                            <li><a href="/conditions-of-use">Conditions of use</a></li>
                            <li><a href="/contactus">Contact us</a></li>
                        </ul>
                    </div>
                </div>
            </footer>
            """;
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestURI().getPath().equals("/")) {
                sendHtml(exchange, 404, "<h1>404 Not Found</h1>");
                return;
            }
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                """ + getCommonHeader() + """
                        <div class="master-wrapper-content">
                            <div class="topic-block-title"><h2>Welcome to our store</h2></div>
                            <div class="product-item">
                                <div class="prices">
                                    <span class="price actual-price">$1,800.00</span>
                                </div>
                            </div>
                        </div>
                """ + getCommonFooter() + """
                    </div>
                </body>
                </html>
                """;
            sendHtml(exchange, 200, html);
        }
    }

    static class CartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Shopping Cart</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page shopping-cart-page">
                                <div class="page-title">
                                    <h1>Shopping cart</h1>
                                </div>
                                <div class="page-body">
                                    <div class="order-summary-content">
                                        <div id="cart-empty-msg" class="no-data" style="display:none;">Your Shopping Cart is empty!</div>
                                        <div id="cart-content-wrapper">
                                            <form method="post" action="/cart" id="shopping-cart-form" onsubmit="return false;">
                                                <div class="table-wrapper">
                                                    <table class="cart">
                                                        <colgroup>
                                                            <col width="1" />
                                                            <col />
                                                            <col width="1" />
                                                            <col width="1" />
                                                            <col width="1" />
                                                        </colgroup>
                                                        <thead>
                                                            <tr>
                                                                <th class="remove-from-cart">Remove</th>
                                                                <th class="product">Product(s)</th>
                                                                <th class="unit-price">Price</th>
                                                                <th class="quantity">Qty.</th>
                                                                <th class="subtotal">Total</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="cart-items-body">
                                                        </tbody>
                                                    </table>
                                                </div>
                                                <div class="cart-options">
                                                    <div class="common-buttons">
                                                        <button type="button" name="updatecart" id="updatecart" class="button-2 update-cart-button" onclick="updateCart();">Update shopping cart</button>
                                                        <button type="button" name="continueshopping" class="button-2 continue-shopping-button" onclick="window.location.href='/';">Continue shopping</button>
                                                    </div>
                                                </div>
                                                <div class="cart-footer">
                                                    <div class="cart-collaterals">
                                                        <div class="deals">
                                                            <div class="coupon-box">
                                                                <div class="title"><strong>Discount Code</strong></div>
                                                                <div class="hint">Enter your coupon here</div>
                                                                <div class="coupon-code">
                                                                    <input name="discountcouponcode" id="discountcouponcode" type="text" class="discount-coupon-code" />
                                                                    <button type="button" name="applydiscountcouponcode" id="applydiscountcouponcode" class="button-2 apply-discount-coupon-code-button" onclick="applyCoupon();">Apply coupon</button>
                                                                </div>
                                                                <div id="discount-coupon-message" class="message" style="display:none;"></div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="totals">
                                                        <div class="order-summary-content">
                                                            <div class="total-info">
                                                                <table class="cart-total">
                                                                    <tbody>
                                                                        <tr class="order-subtotal">
                                                                            <td class="cart-total-left"><label>Sub-Total:</label></td>
                                                                            <td class="cart-total-right"><span class="value-summary" id="cart-order-subtotal">$0.00</span></td>
                                                                        </tr>
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                            <div class="terms-of-service">
                                                                <input id="termsofservice" type="checkbox" name="termsofservice" />
                                                                <label for="termsofservice">I agree with the terms of service</label>
                                                            </div>
                                                            <div class="checkout-buttons">
                                                                <button type="button" id="checkout" name="checkout" class="button-1 checkout-button">Checkout</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                    <script>
                    function renderCart() {
                        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                        var emptyMsg = document.getElementById('cart-empty-msg');
                        var contentWrap = document.getElementById('cart-content-wrapper');
                        var tbody = document.getElementById('cart-items-body');
                        var subtotalEl = document.getElementById('cart-order-subtotal');

                        if (!cart || cart.length === 0) {
                            if (emptyMsg) emptyMsg.style.display = 'block';
                            if (contentWrap) contentWrap.style.display = 'none';
                            return;
                        }

                        if (emptyMsg) emptyMsg.style.display = 'none';
                        if (contentWrap) contentWrap.style.display = 'block';

                        if (tbody) {
                            tbody.innerHTML = '';
                            var total = 0;
                            cart.forEach(function(item) {
                                var lineTotal = item.price * item.qty;
                                total += lineTotal;
                                var tr = document.createElement('tr');
                                tr.innerHTML = '<td class="remove-from-cart">' +
                                    '<button type="button" class="remove-btn" onclick="removeCartItem(' + item.id + ');">x</button>' +
                                    '</td>' +
                                    '<td class="product">' +
                                    '<span class="product-name"><a href="/apple-macbook-pro">' + item.name + '</a></span>' +
                                    '</td>' +
                                    '<td class="unit-price">' +
                                    '<span class="product-unit-price">$' + item.price.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2}) + '</span>' +
                                    '</td>' +
                                    '<td class="quantity">' +
                                    '<input type="text" name="itemquantity' + item.id + '" value="' + item.qty + '" class="qty-input" data-id="' + item.id + '" />' +
                                    '</td>' +
                                    '<td class="subtotal">' +
                                    '<span class="product-subtotal">$' + lineTotal.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2}) + '</span>' +
                                    '</td>';
                                tbody.appendChild(tr);
                            });
                            if (subtotalEl) {
                                subtotalEl.innerText = '$' + total.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2});
                            }
                        }
                    }

                    function removeCartItem(id) {
                        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                        cart = cart.filter(function(item) { return item.id !== id; });
                        localStorage.setItem('cart', JSON.stringify(cart));
                        if (typeof syncHeaderBadges === 'function') syncHeaderBadges();
                        renderCart();
                    }

                    function updateCart() {
                        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                        var inputs = document.querySelectorAll('#cart-items-body input.qty-input');
                        var updated = [];
                        inputs.forEach(function(input) {
                            var id = parseInt(input.getAttribute('data-id'));
                            var val = parseInt(input.value.trim());
                            if (isNaN(val)) val = 0;
                            if (val > 0) {
                                var item = cart.find(function(x) { return x.id === id; });
                                if (item) {
                                    item.qty = val;
                                    updated.push(item);
                                }
                            }
                        });
                        localStorage.setItem('cart', JSON.stringify(updated));
                        if (typeof syncHeaderBadges === 'function') syncHeaderBadges();
                        renderCart();
                    }

                    function applyCoupon() {
                        var code = document.getElementById('discountcouponcode').value.trim();
                        var msg = document.getElementById('discount-coupon-message');
                        if (!code) {
                            if (msg) msg.style.display = 'none';
                            return;
                        }
                        if (msg) {
                            msg.className = 'message-failure';
                            msg.innerText = 'The entered coupon code cannot be found';
                            msg.style.display = 'block';
                        }
                    }

                    renderCart();
                    document.addEventListener('DOMContentLoaded', renderCart);
                    </script>
                </body>
                </html>
                """, getCommonHeader(), getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class WishlistHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String host = exchange.getRequestHeaders().getFirst("Host");
            if (host == null || host.isEmpty()) {
                host = "127.0.0.1:" + port;
            }
            String shareUrl = "http://" + host + "/wishlist/4e17578b-3fb1-432a-bc91-a18544d65017";

            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Wishlist</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page wishlist-page">
                                <div class="page-title">
                                    <h1>Wishlist</h1>
                                </div>
                                <div class="page-body">
                                    <div class="wishlist-content">
                                        <div id="wishlist-empty-msg" class="no-data" style="display:none;">The wishlist is empty!</div>
                                        <div id="wishlist-content-wrapper">
                                            <form method="post" action="/wishlist" id="wishlist-form" onsubmit="return false;">
                                                <div class="table-wrapper">
                                                    <table class="cart">
                                                        <thead>
                                                            <tr>
                                                                <th class="add-to-cart">Add to cart</th>
                                                                <th class="remove-from-cart">Remove</th>
                                                                <th class="product">Product(s)</th>
                                                                <th class="unit-price">Price</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="wishlist-items-body">
                                                        </tbody>
                                                    </table>
                                                </div>
                                                <div class="buttons">
                                                    <button type="button" name="addtocartbutton" class="button-2 wishlist-add-to-cart-button" onclick="transferToCart();">Add to cart</button>
                                                </div>
                                            </form>
                                        </div>
                                        <div class="share-info">
                                            <span class="share-label">Your wishlist URL for sharing:</span>
                                            <a href="%s" class="share-link">%s</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                    <script>
                    function renderWishlist() {
                        var wish = JSON.parse(localStorage.getItem('wishlist') || '[]');
                        var emptyMsg = document.getElementById('wishlist-empty-msg');
                        var contentWrap = document.getElementById('wishlist-content-wrapper');
                        var tbody = document.getElementById('wishlist-items-body');

                        if (!wish || wish.length === 0) {
                            if (emptyMsg) emptyMsg.style.display = 'block';
                            if (contentWrap) contentWrap.style.display = 'none';
                            return;
                        }

                        if (emptyMsg) emptyMsg.style.display = 'none';
                        if (contentWrap) contentWrap.style.display = 'block';

                        if (tbody) {
                            tbody.innerHTML = '';
                            wish.forEach(function(item) {
                                var tr = document.createElement('tr');
                                tr.innerHTML = '<td class="add-to-cart">' +
                                    '<input type="checkbox" name="addtocart" value="' + item.id + '" />' +
                                    '</td>' +
                                    '<td class="remove-from-cart">' +
                                    '<button type="button" class="remove-btn" onclick="removeWishlistItem(' + item.id + ');">x</button>' +
                                    '</td>' +
                                    '<td class="product">' +
                                    '<span class="product-name"><a href="/apple-macbook-pro">' + item.name + '</a></span>' +
                                    '</td>' +
                                    '<td class="unit-price">' +
                                    '<span class="product-unit-price">$' + item.price.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2}) + '</span>' +
                                    '</td>';
                                tbody.appendChild(tr);
                            });
                        }
                    }

                    function removeWishlistItem(id) {
                        var wish = JSON.parse(localStorage.getItem('wishlist') || '[]');
                        wish = wish.filter(function(item) { return item.id !== id; });
                        localStorage.setItem('wishlist', JSON.stringify(wish));
                        if (typeof syncHeaderBadges === 'function') syncHeaderBadges();
                        renderWishlist();
                    }

                    function transferToCart() {
                        var wish = JSON.parse(localStorage.getItem('wishlist') || '[]');
                        var cart = JSON.parse(localStorage.getItem('cart') || '[]');
                        var checkedBoxes = document.querySelectorAll('#wishlist-items-body input[name="addtocart"]:checked');
                        var transferredIds = [];
                        checkedBoxes.forEach(function(chk) {
                            var id = parseInt(chk.value);
                            transferredIds.push(id);
                            var item = wish.find(function(x) { return x.id === id; });
                            if (item) {
                                var existing = cart.find(function(c) { return c.id === id; });
                                if (existing) {
                                    existing.qty += 1;
                                } else {
                                    cart.push({ id: item.id, name: item.name, price: item.price, qty: 1 });
                                }
                            }
                        });
                        wish = wish.filter(function(w) { return transferredIds.indexOf(w.id) === -1; });
                        localStorage.setItem('wishlist', JSON.stringify(wish));
                        localStorage.setItem('cart', JSON.stringify(cart));
                        if (typeof syncHeaderBadges === 'function') syncHeaderBadges();
                        window.location.href = '/cart';
                    }

                    renderWishlist();
                    document.addEventListener('DOMContentLoaded', renderWishlist);
                    </script>
                </body>
                </html>
                """, getCommonHeader(), shareUrl, shareUrl, getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Login</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page login-page">
                                <div class="page-title">
                                    <h1>Welcome, Please Sign In!</h1>
                                </div>
                                <div class="customer-blocks">
                                    <div class="returning-wrapper fieldset">
                                        <div class="title">
                                            <strong>Returning Customer</strong>
                                        </div>
                                        <div class="message-error validation-summary-errors" id="login-summary-error" style="display:none;"></div>
                                        <form action="/login" method="post" novalidate="novalidate" onsubmit="
                                            var email = document.getElementById('Email').value.trim();
                                            var emailErr = document.getElementById('Email-error');
                                            var summaryErr = document.getElementById('login-summary-error');
                                            if (emailErr) { emailErr.innerText = ''; emailErr.style.display = 'none'; }
                                            if (summaryErr) { summaryErr.innerText = ''; summaryErr.style.display = 'none'; }
                                            if (!email) {
                                                if (emailErr) { emailErr.innerText = 'Please enter your email'; emailErr.style.display = 'inline-block'; }
                                                return false;
                                            }
                                            if (email.indexOf('@') === -1) {
                                                if (emailErr) { emailErr.innerText = 'Wrong email'; emailErr.style.display = 'inline-block'; }
                                                return false;
                                            }
                                            if (summaryErr) {
                                                summaryErr.innerText = 'Login was unsuccessful. Please correct the errors and try again. No customer account found';
                                                summaryErr.style.display = 'block';
                                            }
                                            return false;
                                        ">
                                            <div class="form-fields">
                                                <div class="inputs">
                                                    <label for="Email">Email:</label>
                                                    <input type="email" class="email" id="Email" name="Email" />
                                                    <span id="Email-error" class="field-validation-error"></span>
                                                </div>
                                                <div class="inputs">
                                                    <label for="Password">Password:</label>
                                                    <input type="password" class="password" id="Password" name="Password" />
                                                </div>
                                                <div class="inputs reversed">
                                                    <span class="forgot-password">
                                                        <a href="/passwordrecovery">Forgot password?</a>
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="buttons">
                                                <button type="submit" class="button-1 login-button">Log in</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, getCommonHeader(), getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class PasswordRecoveryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Password Recovery</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page password-recovery-page">
                                <div class="page-title">
                                    <h1>Password recovery</h1>
                                </div>
                                <div class="page-body">
                                    <p class="result" id="recovery-result" style="display:none;"></p>
                                    <form action="/passwordrecovery" method="post" novalidate="novalidate" onsubmit="
                                        var email = document.getElementById('Email').value.trim();
                                        var emailErr = document.getElementById('Email-error');
                                        var res = document.getElementById('recovery-result');
                                        var bar = document.getElementById('bar-notification');
                                        if (emailErr) { emailErr.innerText = ''; emailErr.style.display = 'none'; }
                                        if (res) { res.innerText = ''; res.style.display = 'none'; }
                                        if (bar) { bar.style.display = 'none'; }
                                        if (!email) {
                                            if (emailErr) { emailErr.innerText = 'Enter your email'; emailErr.style.display = 'inline-block'; }
                                            return false;
                                        }
                                        if (email.indexOf('@') === -1) {
                                            if (emailErr) { emailErr.innerText = 'Wrong email'; emailErr.style.display = 'inline-block'; }
                                            return false;
                                        }
                                        if (res) {
                                            res.innerText = 'Email with instructions has been sent to you.';
                                            res.style.display = 'block';
                                        }
                                        if (bar) {
                                            bar.style.display = 'block';
                                            var barP = bar.querySelector('p.content');
                                            if (barP) barP.innerText = 'Email with instructions has been sent to you.';
                                        }
                                        return false;
                                    ">
                                        <div class="inputs">
                                            <label for="Email">Your email address:</label>
                                            <input type="email" class="email" id="Email" name="Email" />
                                            <span id="Email-error" class="field-validation-error"></span>
                                        </div>
                                        <div class="buttons">
                                            <button type="submit" name="send-email" class="button-1 password-recovery-button">Recover</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, getCommonHeader(), getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Register</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page registration-page">
                                <div class="page-title">
                                    <h1>Register</h1>
                                </div>
                                <div class="page-body">
                                    <div class="message-error" id="registration-summary-error" style="display:none;"></div>
                                    <div class="result" id="registration-result" style="display:none;">Your registration completed</div>
                                    <div id="registration-form-wrapper">
                                        <form action="/register" method="post" novalidate="novalidate" onsubmit="
                                            var fn = document.getElementById('FirstName').value.trim();
                                            var ln = document.getElementById('LastName').value.trim();
                                            var em = document.getElementById('Email').value.trim();
                                            var pw = document.getElementById('Password').value;
                                            var cp = document.getElementById('ConfirmPassword').value;

                                            var fnErr = document.getElementById('FirstName-error');
                                            var lnErr = document.getElementById('LastName-error');
                                            var emErr = document.getElementById('Email-error');
                                            var pwErr = document.getElementById('Password-error');
                                            var cpErr = document.getElementById('ConfirmPassword-error');
                                            var sumErr = document.getElementById('registration-summary-error');
                                            var resBlock = document.getElementById('registration-result');
                                            var formWrap = document.getElementById('registration-form-wrapper');

                                            if (fnErr) fnErr.innerText = '';
                                            if (lnErr) lnErr.innerText = '';
                                            if (emErr) emErr.innerText = '';
                                            if (pwErr) pwErr.innerText = '';
                                            if (cpErr) cpErr.innerText = '';
                                            if (sumErr) { sumErr.innerText = ''; sumErr.style.display = 'none'; }

                                            if (!fn && !ln && !em) {
                                                if (fnErr) fnErr.innerText = 'First name is required.';
                                                if (lnErr) lnErr.innerText = 'Last name is required.';
                                                if (emErr) emErr.innerText = 'Email is required.';
                                                return false;
                                            }
                                            if (!fn) { if (fnErr) fnErr.innerText = 'First name is required.'; return false; }
                                            if (!ln) { if (lnErr) lnErr.innerText = 'Last name is required.'; return false; }
                                            if (!em) { if (emErr) emErr.innerText = 'Email is required.'; return false; }

                                            if (em.indexOf('@') === -1) {
                                                if (emErr) emErr.innerText = 'Wrong email';
                                                return false;
                                            }
                                            if (em === 'admin@yourstore.com') {
                                                if (sumErr) {
                                                    sumErr.innerText = 'The specified email already exists';
                                                    sumErr.style.display = 'block';
                                                }
                                                return false;
                                            }
                                            if (pw.length < 6) {
                                                if (pwErr) pwErr.innerText = 'must have at least 6 characters';
                                                return false;
                                            }
                                            if (pw !== cp) {
                                                if (cpErr) cpErr.innerText = 'The password and confirmation password do not match.';
                                                return false;
                                            }
                                            if (formWrap) formWrap.style.display = 'none';
                                            if (resBlock) {
                                                resBlock.innerText = 'Your registration completed';
                                                resBlock.style.display = 'block';
                                            }
                                            return false;
                                        ">
                                            <div class="fieldset">
                                                <div class="title"><strong>Your Personal Details</strong></div>
                                                <div class="form-fields">
                                                    <div class="inputs">
                                                        <label>Gender:</label>
                                                        <input type="radio" value="M" id="gender-male" name="Gender" />
                                                        <label class="forcheckbox" for="gender-male">Male</label>
                                                        <input type="radio" value="F" id="gender-female" name="Gender" />
                                                        <label class="forcheckbox" for="gender-female">Female</label>
                                                    </div>
                                                    <div class="inputs">
                                                        <label for="FirstName">First name:</label>
                                                        <input type="text" id="FirstName" name="FirstName" />
                                                        <span id="FirstName-error" class="field-validation-error"></span>
                                                    </div>
                                                    <div class="inputs">
                                                        <label for="LastName">Last name:</label>
                                                        <input type="text" id="LastName" name="LastName" />
                                                        <span id="LastName-error" class="field-validation-error"></span>
                                                    </div>
                                                    <div class="inputs">
                                                        <label for="Email">Email:</label>
                                                        <input type="email" id="Email" name="Email" />
                                                        <span id="Email-error" class="field-validation-error"></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="fieldset">
                                                <div class="title"><strong>Your Password</strong></div>
                                                <div class="form-fields">
                                                    <div class="inputs">
                                                        <label for="Password">Password:</label>
                                                        <input type="password" id="Password" name="Password" />
                                                        <span id="Password-error" class="field-validation-error"></span>
                                                    </div>
                                                    <div class="inputs">
                                                        <label for="ConfirmPassword">Confirm password:</label>
                                                        <input type="password" id="ConfirmPassword" name="ConfirmPassword" />
                                                        <span id="ConfirmPassword-error" class="field-validation-error"></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="buttons">
                                                <button type="submit" id="register-button" class="button-1 register-next-step-button">Register</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, getCommonHeader(), getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class CategoryHandler implements HttpHandler {
        private final String categoryName;
        public CategoryHandler(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. """ + categoryName + """
                    </title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                """ + getCommonHeader() + """
                        <div class="master-wrapper-content">
                            <div class="page category-page">
                                <div class="page-title"><h1>""" + categoryName + """
                                </h1></div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
            sendHtml(exchange, 200, html);
        }
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String rawQuery = exchange.getRequestURI().getRawQuery();
            String q = "";
            if (rawQuery != null) {
                for (String param : rawQuery.split("&")) {
                    String[] parts = param.split("=", 2);
                    if (parts.length > 0 && parts[0].equals("q")) {
                        if (parts.length > 1) {
                            try {
                                q = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                            } catch (Exception e) {
                                q = parts[1];
                            }
                        }
                        break;
                    }
                }
            }

            String normalized = q.trim().toLowerCase();
            String searchResultsHtml;

            if (normalized.contains("macbook") || normalized.contains("apple")) {
                searchResultsHtml = """
                    <div class="item-box">
                        <div class="product-item" data-productid="4">
                            <h2 class="product-title">
                                <a href="/apple-macbook-pro">Apple MacBook Pro 13-inch</a>
                            </h2>
                        </div>
                    </div>
                    """;
            } else if (normalized.contains("build")) {
                searchResultsHtml = """
                    <div class="item-box">
                        <div class="product-item" data-productid="1">
                            <h2 class="product-title">
                                <a href="/apple-macbook-pro">Build your own computer</a>
                            </h2>
                        </div>
                    </div>
                    """;
            } else {
                searchResultsHtml = """
                    <div class="no-result">
                        No products were found that matched your criteria.
                    </div>
                    """;
            }

            String escapedQ = q.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");

            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Search</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page search-page">
                                <div class="page-title">
                                    <h1>Search</h1>
                                </div>
                                <div class="page-body">
                                    <div class="search-input">
                                        <form action="/search" method="get" onsubmit="var val=document.getElementById('q').value.trim();if(!val){alert('Please enter some search keyword');return false;}">
                                            <input type="text" class="search-text" id="q" name="q" value="%s" />
                                            <button type="submit" class="button-1 search-button">Search</button>
                                        </form>
                                    </div>
                                    <div class="search-results">
                                        %s
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """, getCommonHeader(), escapedQ, searchResultsHtml);
            sendHtml(exchange, 200, html);
        }
    }

    static class ProductHandler implements HttpHandler {
        private final String productName;

        public ProductHandler(String productName) {
            this.productName = productName;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. %s</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page product-details-page">
                                <div class="breadcrumb">
                                    <ul>
                                        <li><a href="/">Home</a></li>
                                        <li><span class="delimiter">/</span></li>
                                        <li><a href="/computers">Computers</a></li>
                                        <li><span class="delimiter">/</span></li>
                                        <li><strong class="current-item">%s</strong></li>
                                    </ul>
                                </div>
                                <div class="product-essential">
                                    <div class="overview">
                                        <div class="product-name">
                                            <h1>%s</h1>
                                        </div>
                                        <div class="product-reviews-overview">
                                            <div class="product-review-links">
                                                <a href="/productreviews/4">2 review(s)</a>
                                            </div>
                                        </div>
                                        <div class="prices">
                                            <div class="product-price">
                                                <span id="price-value-4">$1,800.00</span>
                                            </div>
                                        </div>
                                        <div class="add-to-cart">
                                            <div class="add-to-cart-panel">
                                                <input type="text" id="product_enteredQuantity_4" class="qty-input" value="1" />
                                                <button type="button" id="add-to-cart-button-4" class="button-1 add-to-cart-button" onclick="addToCart(4, 'Apple MacBook Pro 13-inch', 1800.00);">Add to cart</button>
                                            </div>
                                        </div>
                                        <div class="overview-buttons">
                                            <div class="add-to-wishlist">
                                                <button type="button" id="add-to-wishlist-button-4" class="button-2 add-to-wishlist-button" onclick="addToWishlist(4, 'Apple MacBook Pro 13-inch', 1800.00);">Add to wishlist</button>
                                            </div>
                                            <div class="compare-products">
                                                <button type="button" class="button-2 add-to-compare-list-button" onclick="showBarNotification('The product has been added to your compare products list');">Add to compare list</button>
                                            </div>
                                            <div class="email-a-friend">
                                                <button type="button" class="button-2 email-a-friend-button">Email a friend</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, productName, getCommonHeader(), productName, productName, getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class ContactUsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Contact Us</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page contact-page">
                                <div class="page-title">
                                    <h1>Contact Us</h1>
                                </div>
                                <div class="page-body">
                                    <form action="/contactus" method="post" onsubmit="var fn=document.getElementById('FullName').value.trim();var em=document.getElementById('Email').value.trim();var en=document.getElementById('Enquiry').value.trim();var err=false;if(!fn){document.getElementById('FullName-error').innerText='Enter your name';err=true;}else{document.getElementById('FullName-error').innerText='';}if(!em){document.getElementById('Email-error').innerText='Enter your email';err=true;}else{document.getElementById('Email-error').innerText='';}if(!en){document.getElementById('Enquiry-error').innerText='Enter enquiry';err=true;}else{document.getElementById('Enquiry-error').innerText='';}if(err)return false;">
                                        <div class="inputs">
                                            <input type="text" id="FullName" name="FullName" class="fullname" />
                                            <span id="FullName-error" class="field-validation-error"></span>
                                        </div>
                                        <div class="inputs">
                                            <input type="email" id="Email" name="Email" class="email" />
                                            <span id="Email-error" class="field-validation-error"></span>
                                        </div>
                                        <div class="inputs">
                                            <textarea id="Enquiry" name="Enquiry" class="enquiry"></textarea>
                                            <span id="Enquiry-error" class="field-validation-error"></span>
                                        </div>
                                        <div class="buttons">
                                            <button type="submit" name="send-email" class="button-1 contact-us-button">Submit</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, getCommonHeader(), getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }

    static class InfoPageHandler implements HttpHandler {
        private final String pageTitle;

        public InfoPageHandler(String pageTitle) {
            this.pageTitle = pageTitle;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. %s</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                        %s
                        <div class="master-wrapper-content">
                            <div class="page topic-page">
                                <div class="page-title">
                                    <h1>%s</h1>
                                </div>
                                <div class="page-body">
                                    <p>Topic content for %s.</p>
                                </div>
                            </div>
                        </div>
                        %s
                    </div>
                </body>
                </html>
                """, pageTitle, getCommonHeader(), pageTitle, pageTitle, getCommonFooter());
            sendHtml(exchange, 200, html);
        }
    }
}
