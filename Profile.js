console.log("Profile.js Loaded");

const profileUrl = "http://localhost:40109/shopping";

// AngularJS App and Controller
angular.module("profileApp", []).controller("profileController", function ($scope) {
  // Logout functionality
  $scope.showLogoutPopup = function () {
    console.log("Showing logout popup");
    const popupHtml = `
      <div class="logout-popup" id="logoutPopup">
        <h3>Are you sure you want to logout?</h3>
        <div class="logout-popup-buttons">
          <button class="logout-btn logout-btn-yes" onclick="confirmLogout()">Yes</button>
          <button class="logout-btn logout-btn-no" onclick="closeLogoutPopup()">No</button>
        </div>
      </div>`;
    document.body.insertAdjacentHTML("beforeend", popupHtml);
  };

  window.confirmLogout = function () {
    console.log("User confirmed logout");
    sessionStorage.clear();
    window.location.href = "login.html";
  };

  window.closeLogoutPopup = function () {
    const popup = document.getElementById("logoutPopup");
    if (popup) {
      popup.remove();
    }
  };

  // Fetch Profile Data
  $scope.fetchProfileData = function () {
    const userId = sessionStorage.getItem("USERID");
    console.log("Retrieved userId:", userId);

    if (!userId) {
      alert("You are not logged in. Redirecting to login page.");
      window.location.href = "login.html";
      return;
    }

    const requestData = {
      userId: userId,
      action: "fetchProfile",
    };

    fetch(profileUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestData),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("Profile data:", data);
        if (data.status === "success") {
          $scope.populateProfile(data.profile);
        } else {
          alert("Failed to fetch profile details. Redirecting to login.");
          window.location.href = "login.html";
        }
      })
      .catch((error) => {
        console.error("Error fetching profile data:", error);
        alert("An error occurred while loading your profile. Please try again.");
      });
  };

  // Populate Profile
  $scope.populateProfile = function (profile) {
    document.getElementById("firstName").innerText = profile.firstName;
    document.getElementById("lastName").innerText = profile.lastName;
    document.getElementById("email").innerText = profile.email;
    document.getElementById("phoneNumber").innerText = profile.phoneNumber;
    document.getElementById("address").innerText = profile.address;
  };

  // Fetch profile data on page load
  $scope.fetchProfileData();
});
