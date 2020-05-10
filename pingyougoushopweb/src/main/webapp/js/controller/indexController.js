app.controller('indexController',function ($scope,loginService,$controller) {

    $controller('baseController',{$scope:$scope});
    $scope.showLoginName=function () {
        loginService.loginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        );
    }


});