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

    // Logout function
    $scope.showLogoutPopup = function () {
        console.log("Showing logout popup");
        const popupHtml = `
            <div class="logout-popup" id="logoutPopup">
                <h3>Are you sure you want to logout?</h3>
                <div class="logout-popup-buttons">
                    <button class="logout-btn logout-btn-yes" onclick="confirmLogout()">Yes</button>
                    <button class="logout-btn logout-btn-no" onclick="closeLogoutPopup()">No</button>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML("beforeend", popupHtml);
    };

    // Confirm logout function
    window.confirmLogout = function () {
        console.log("User confirmed logout");
        sessionStorage.clear();
        window.location.href = "login.html";
    };

    // Close logout popup
    window.closeLogoutPopup = function () {
        console.log("Closing logout popup");
        const popup = document.getElementById("logoutPopup");
        if (popup) {
            popup.remove();
        }
    };
});
