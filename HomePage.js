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

    $scope.addToCart = function (productId, productName, productPrice) {
        console.log("Adding product to cart:", productId);
    
        const userId = sessionStorage.getItem("USERID");
        if (!userId) {
            alert("You are not logged in. Please log in first.");
            return;
        }
    
        const requestData = {
            userId: userId,
            productId: productId,
            productName: productName,
            productPrice: productPrice,
            action: "addToCart"
        };
    
        // Send the data to the backend API
        fetch("http://localhost:40109/shopping", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(requestData),
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.status === "success") {
                    // Update cart notification badge
                    updateCartBadge(data.cartItemCount);
                    alert("Product added to cart!");
                } else {
                    alert("Failed to add product to cart. Please try again.");
                    console.error(data.message);
                }
            })
            .catch((error) => console.error("Error adding product to cart:", error));
    };
    
    // Update cart badge with item count
    function updateCartBadge(itemCount) {
        const cartBadge = document.getElementById("cartBadge");
        if (cartBadge) {
            cartBadge.textContent = itemCount;
            cartBadge.style.display = itemCount > 0 ? "block" : "none";
        }
    }

});


