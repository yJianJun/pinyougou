app.controller('contentController',function ($scope,contentService) {

    $scope.contentList=[];
    $scope.findByCategoryId=function (catrgoryId) {
        contentService.findByCategoryId(catrgoryId).success(
            function (response) {
                $scope.contentList[catrgoryId]=response;

            }
        );
    }

    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;

    }

});