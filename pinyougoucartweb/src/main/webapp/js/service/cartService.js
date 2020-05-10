app.service('cartService',function ($http) {

    this.queryCartList=function () {
        return $http.get('cart/queryCartList.do');
    }

    this.addGoodsToCartList=function (itemId,num) {
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }


    this.sum=function (cartList) {

        var totalValue={totalNum:0,totalMoney:0};

        for (var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j =0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    }

    this.findAddressList=function () {
        return $http.get('address/queryListByLoginUser.do');
    }

    this.submitOrder=function (order) {
        return $http.post('order/add.do',order);
    }

});