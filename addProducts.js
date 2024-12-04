console.log("Add Products Page Loaded");

const adminUrl = "http://localhost:40109/admin";

angular.module("adminApp", []).controller("addProductController", function ($scope) {
  $scope.product = {
    name: "",
    price: "",
  };

  // Add Product Function
  $scope.addProduct = function () {
    if (!$scope.product.name || !$scope.product.price) {
      alert("Please fill in all fields.");
      return;
    }

    const requestData = {
      name: $scope.product.name,
      price: $scope.product.price,
      image: $scope.product.image,
      action: "addProduct",
    };

    fetch(adminUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(requestData),
    })
      .then((response) => response.json())
      .then((data) => {
        alert(data.message || "Product added successfully!");
        $scope.$apply(() => {
          $scope.product.name = "";
          $scope.product.price = "";
        });
      })
      .catch((error) => console.error("Error:", error));
  };

  // Logout Function
  $scope.logout = function () {
    sessionStorage.clear();
    window.location.href = "adminlogin.html";
  };
});
