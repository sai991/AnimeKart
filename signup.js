console.log("Signup.js Loaded");

// URL for Signup
let signupUrl = "http://localhost:40109/shopping";

angular.module("signupApp", []).controller("signupController", function ($scope) {
    $scope.user = {}; // Initialize user object
    $scope.errors = {}; // Initialize errors object

    $scope.submitSignup = function () {
        console.log("Signup Attempt");
        $scope.errors = {}; // Clear previous errors

        if (validateSignup($scope)) {
            let signupData = {
                firstName: $scope.user.firstName,
                lastName: $scope.user.lastName,
                email: $scope.user.email,
                phoneNumber: $scope.user.phoneNumber,
                address: $scope.user.address,
                password: $scope.user.password,
                action: "signup",
            };

            console.log("Signup Data Validated:", signupData);
            sendSignupData(signupData);
        } else {
            console.log("Invalid input in signup");
        }
    };

    function validateSignup($scope) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const phonePattern = /^\d{10}$/;

        let isValid = true;

        if (!$scope.user.firstName?.trim()) {
            $scope.errors.firstName = "First name cannot be empty.";
            isValid = false;
        }

        if (!$scope.user.lastName?.trim()) {
            $scope.errors.lastName = "Last name cannot be empty.";
            isValid = false;
        }

        if (!emailPattern.test($scope.user.email)) {
            $scope.errors.email = "Please enter a valid email address.";
            isValid = false;
        }

        if (!phonePattern.test($scope.user.phoneNumber)) {
            $scope.errors.phoneNumber = "Please enter a valid 10-digit phone number.";
            isValid = false;
        }

        if (!$scope.user.address?.trim()) {
            $scope.errors.address = "Address cannot be empty.";
            isValid = false;
        }

        if (!$scope.user.password || $scope.user.password.length < 8) {
            $scope.errors.password = "Password must be at least 8 characters long.";
            isValid = false;
        }

        if ($scope.user.password !== $scope.user.confirmPassword) {
            $scope.errors.confirmPassword = "Passwords do not match.";
            isValid = false;
        }

        return isValid;
    }

    function sendSignupData(signupData) {
        let req = new XMLHttpRequest();
        req.addEventListener("load", signupResponseHandler);
        req.open("POST", signupUrl);
        req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        req.send(JSON.stringify(signupData));
        console.log("Sent to server: json =", JSON.stringify(signupData));
    }

    function signupResponseHandler() {
        let response = JSON.parse(this.responseText);
        if (response.status === "success") {
            console.log("Signup Successful");
            alert("Signup completed! Redirecting to login page.");
            window.location.href = "login.html";
        } else {
            console.log("Signup Failed");
            alert(response.message || "Signup failed, please try again.");
        }
    }
});
