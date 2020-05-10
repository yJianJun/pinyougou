app.controller('itemController',function ($scope,$http) {
	
	$scope.specificationItems={};
	
	$scope.addNum=function(x){
		$scope.num+=x;			
		if($scope.num<1){
			$scope.num=1;
		}
	}
	
	$scope.selectspecification=function(key,value){
		
		$scope.specificationItems[key]=value;
		$scope.searchSku();
	}
	
	$scope.isSelected=function(key,value){
		
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};
	
	$scope.loadSku=function(){
		
		$scope.sku=skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
		
	}
	
	
	matchObject=function(map1,map2){
		
		for(var k in map1){
				
			if(map1[k]!=map2[k]){
				
				return false;
			}
		}
		
		for(var k in map2){
				
			if(map2[k]!=map1[k]){
				
				return false;
			}
		}
		return true;
	}
	
	$scope.searchSku=function(){
		
		for(var i=0;i<skuList.length;i++){
			
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'-----',price:0};
	}
	
	$scope.addToCart=function(){
		// alert('SKUID:'+$scope.sku.id);
		$http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
			+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(
				function (response) {
					if(response.success){
						location.href='http://localhost:9107/cart.html';
					}else {
						alert(response.message);
					}
                }
		);

	}
	
	

});