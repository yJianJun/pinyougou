app.service('searchService',function ($http) {

    this.search=function (searchmap) {
         return $http.post('itemsearch/search.do',searchmap);
    }
});