console.log("History.js Loaded");

const historyUrl = "http://localhost:40109/shopping";

// Define AngularJS Application and Controller
angular.module("history", []).controller("historyController", function ($scope) {
  $scope.orderHistory = []; // Initialize order history
  $scope.isLoading = true; // Show a loading state

  // Logout functionality
  $scope.showLogoutPopup = function () {
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

  window.confirmLogout = function () {
    sessionStorage.clear();
    window.location.href = "login.html";
  };

  window.closeLogoutPopup = function () {
    const popup = document.getElementById("logoutPopup");
    if (popup) {
      popup.remove();
    }
  };

  // Fetch order history
  $scope.fetchOrderHistory = function () {
    const userId = sessionStorage.getItem("USERID");
    if (!userId) {
      alert("You are not logged in. Redirecting to login page.");
      window.location.href = "login.html";
      return;
    }

    const requestData = { userId, action: "fetchOrderHistory" };

    fetch(historyUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(requestData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Failed to fetch order history: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("Order History Data:", data);
        $scope.$apply(() => {
            if (data.length > 0) {
              $scope.orderHistory = data; // Directly use the array `data`
            } else {
              $scope.orderHistory = [];
            }
            $scope.isLoading = false;
          });
      })
      .catch((error) => {
        console.error("Error fetching order history:", error);
        $scope.$apply(() => {
          $scope.isLoading = false;
        });
      });
  };

  $scope.fetchOrderHistory(); // Fetch on page load
});
