console.log("Profile.js Loaded");

const profileUrl = "http://localhost:40109/shopping";

angular.module("profileApp", []).controller("profileController", function ($scope) {
  $scope.profile = {};
  $scope.isEditing = false; // Track edit mode
  $scope.successMessage = "";
  $scope.errorMessage = "";

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
      .then((response) => response.json())
      .then((data) => {
        console.log("Profile data:", data);
        if (data.status === "success") {
          $scope.$apply(() => {
            $scope.profile = data.profile;
          });
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

  // Toggle edit mode
  $scope.toggleEditMode = function () {
    $scope.isEditing = !$scope.isEditing;
    if (!$scope.isEditing) {
      // Save changes
      $scope.saveProfile();
    }
  };

  // Save Profile Data
  $scope.saveProfile = function () {
    const userId = sessionStorage.getItem("USERID");

    if (!userId) {
      alert("You are not logged in. Redirecting to login page.");
      window.location.href = "login.html";
      return;
    }

    const requestData = {
      userId: userId,
      action: "updateProfile",
      profile: $scope.profile,
      firstName:$scope.profile.firstName,
      lastName:$scope.profile.lastName,
      email:$scope.profile.email,
      phoneNumber: $scope.profile.phoneNumber,
      address:$scope.profile.address,
    };

    fetch(profileUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestData),
    })
      .then((response) => response.json())
      .then((data) => {
        console.log("Save Profile Response:", data);
        if (data.status === "success") {
          $scope.$apply(() => {
            $scope.successMessage = "Profile updated successfully!";
            $scope.errorMessage = "";
          });
        } else {
          $scope.$apply(() => {
            $scope.errorMessage = "Failed to update profile. Please try again.";
            $scope.successMessage = "";
          });
        }
      })
      .catch((error) => {
        console.error("Error updating profile:", error);
        $scope.$apply(() => {
          $scope.errorMessage = "An error occurred while updating profile.";
          $scope.successMessage = "";
        });
      });
  };

  // Fetch profile data on page load
  $scope.fetchProfileData();
});
