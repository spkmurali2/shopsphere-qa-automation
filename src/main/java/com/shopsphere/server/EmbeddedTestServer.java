package com.shopsphere.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
            server.createContext("/cart", new CartHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/register", new RegisterHandler());
            server.createContext("/computers", new CategoryHandler("Computers"));
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
                    <form action="/search" method="get">
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
                    <select id="customerCurrency" name="customerCurrency">
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
                    <span class="close" title="Close">&nbsp;</span>
                </div>
            </header>
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
                        </div>
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
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Shopping Cart</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                """ + getCommonHeader() + """
                        <div class="master-wrapper-content">
                            <div class="page shopping-cart-page">
                                <div class="page-title"><h1>Shopping cart</h1></div>
                                <div class="page-body">
                                    <div class="order-summary-content">
                                        <div class="no-data">Your Shopping Cart is empty!</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
            sendHtml(exchange, 200, html);
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Login</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                """ + getCommonHeader() + """
                        <div class="master-wrapper-content">
                            <div class="page login-page">
                                <form action="/login" method="post">
                                    <input type="email" class="email" id="Email" name="Email" />
                                    <input type="password" class="password" id="Password" name="Password" />
                                    <button type="submit" class="button-1 login-button">Log in</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
            sendHtml(exchange, 200, html);
        }
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>nopCommerce demo store. Register</title>
                </head>
                <body>
                    <div class="master-wrapper-page">
                """ + getCommonHeader() + """
                        <div class="master-wrapper-content">
                            <div class="page registration-page">
                                <form action="/register" method="post">
                                    <input type="text" id="FirstName" name="FirstName" />
                                    <input type="text" id="LastName" name="LastName" />
                                    <input type="email" id="Email" name="Email" />
                                    <input type="password" id="Password" name="Password" />
                                    <input type="password" id="ConfirmPassword" name="ConfirmPassword" />
                                    <button type="submit" id="register-button" class="button-1 register-next-step-button">Register</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
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
}
