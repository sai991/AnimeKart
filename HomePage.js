console.log("HomePage.js Loaded");

// URL for fetching products
const productsUrl = "http://localhost:40109/products";

angular.module("homeApp", []).controller("homeController", function ($scope, $http) {
    $scope.products = [];

    // Fetch products from the server
    $http.get(productsUrl).then(
        function (response) {
            $scope.products = response.data; // Populate the products array
            console.log("Products loaded:", $scope.products);
        },
        function (error) {
            console.error("Error fetching products:", error);
        }
    );

    // Add to cart function
    $scope.addToCart = function (productId) {
        console.log("Adding product to cart:", productId);

        // Logic to send productId to cart API
        alert("Product added to cart!");
    };
});
