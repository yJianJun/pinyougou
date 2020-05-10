app.controller('baseController',function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    $scope.ids = [];

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1);
        }
    }

    $scope.domain = {};

    $scope.jsonToString = function (jsonString, key) {

        var json = JSON.parse(jsonString);
        var value = "";

        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

    $scope.searchObjectByKey=function (list,keyName,keyValue) {
        for (var i=0;i<list.length;i++){
            if(list[i][keyName]==keyValue){
                return list[i];
            }
        }
        return null;
    }

});