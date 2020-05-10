app.controller('cartController',function ($scope,cartService) {

    $scope.queryCartList=function () {
        cartService.queryCartList().success(

            function (response) {

                $scope.cartList=response;
                $scope.totalValue=cartService.sum($scope.cartList);
                $scope.totalNum=$scope.totalValue.totalNum;
                $scope.totalMoney=$scope.totalValue.totalMoney;
            }
        );
    }

    $scope.addGoodsToCartList=function (itemId,num) {

        cartService.addGoodsToCartList(itemId,num).success(
          
            function (response) {
                if (response.success){
                    $scope.queryCartList();
                }else {
                    alert(response.message);
                }
            }
        );
    }

     // sum=function () {
     //    $scope.totalNum=0;
     //    $scope.totalMoney=0;
     //
     //    for (var i=0;i<$scope.cartList.length;i++){
     //        var cart=$scope.cartList[i];
     //        for(var j =0;j<cart.orderItemList.length;j++){
     //            var orderItem=cart.orderItemList[j];
     //            $scope.totalNum+=orderItem.num;
     //            $scope.totalMoney+=orderItem.totalFee;
     //        }
     //    }
     //
     // }


    $scope.findAddressList=function () {
        cartService.findAddressList().success(
          function (response) {

              $scope.addressList=response;
              for (var i=0;i<$scope.addressList.length;i++){
                  if($scope.addressList[i].isDefault=='1'){
                      $scope.address=$scope.addressList[i];
                      break;
                  }
              }
          }
        );
    }


    $scope.selectAddress=function (address) {

        $scope.address=address;
    }

    $scope.isSelectedAddress=function (address) {
        if (address==$scope.address){

            return true;
        }else {
            return false;
        }
    }

    $scope.order={paymentType:'1'};

    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }

    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;

        cartService.submitOrder($scope.order).success(
          function (response) {

              if (response.success){
                  if($scope.order.paymentType=='1'){
                      location.href="pay.html";
                  }else {
                      location.href="paysuccess.html";
                  }
              }else {
                  alert(response.message);
              }
          }

        );
    }

});