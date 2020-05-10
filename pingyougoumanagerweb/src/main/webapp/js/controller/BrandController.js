app.controller('brandController', function ($scope, brandService,$controller) {

    $controller('baseController',{$scope:$scope});

    $scope.queryAll = function () {
        brandService.queryAll('../brand/queryAll.do').success(
            function (response) {
                $scope.list = response;
            }
        );
    }
    // 分页控件配置

    $scope.fillPage = function (page, size) {
        brandService.fillPage(page, size).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }

    $scope.save = function () {
        var obj = null;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        );
    }

    $scope.queryOne = function (id) {
        brandService.queryOne(id).success(
            function (reponse) {
                $scope.entity = reponse;
            }
        );
    }

    $scope.remove = function () {
        brandService.remove($scope.ids).success(
            function (reponse) {
                if (reponse.success) {
                    $scope.reloadList();
                } else {
                    alert(reponse.message);
                }
            }
        );
    }

    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.domain).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            }
        );
    }
    

});
