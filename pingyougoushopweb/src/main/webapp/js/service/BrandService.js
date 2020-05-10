app.service("brandService", function ($http) {
    this.queryAll = function (url) {
        return $http.get(url);
    }

    this.fillPage = function (page, size) {
        return $http.get('../brand/fillPage.do?page=' + page + '&size=' + size);
    }
    this.queryOne = function (id) {
        return $http.get('../brand/queryOne.do?id=' + id);
    }
    this.remove = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    }
    this.add = function (entity) {
        return $http.post('../brand/add.do', entity);
    }
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    }
    this.search = function (page, size, domain) {
        return $http.post('../brand/search.do?page=' + page + '&size=' + size, domain);
    }

    this.selectOptionList=function () {
        return $http.get('../brand/selectOptionList.do');
    }


});