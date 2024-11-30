console.log("Signup.js Loaded");

// URL for Signup
let signupUrl = "http://localhost:40109/shopping";

angular.module("signupApp", []).controller("signupController", function ($scope) {
    $scope.user = {}; // Initialize user object

    $scope.submitSignup = function () {
        console.log("Signup Attempt");
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
            alert("Please correct the highlighted errors.");
        }
    };
});

function validateSignup($scope) {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phonePattern = /^\d{10}$/;

    let isValid = true;

    const fields = [
        { field: "firstName", condition: !$scope.user.firstName?.trim() },
        { field: "lastName", condition: !$scope.user.lastName?.trim() },
        { field: "email", condition: !emailPattern.test($scope.user.email) },
        { field: "phoneNumber", condition: !phonePattern.test($scope.user.phoneNumber) },
        { field: "address", condition: !$scope.user.address?.trim() },
        { field: "password", condition: !$scope.user.password || $scope.user.password.length < 8 },
        { field: "confirmPassword", condition: $scope.user.password !== $scope.user.confirmPassword },
    ];

    fields.forEach(({ field, condition }) => {
        if (condition) {
            highlightError(field);
            isValid = false;
        } else {
            clearError(field);
        }
    });

    return isValid;
}

function highlightError(fieldId) {
    const element = document.getElementById(fieldId);
    if (element) element.classList.add("error");
}

function clearError(fieldId) {
    const element = document.getElementById(fieldId);
    if (element) element.classList.remove("error");
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
