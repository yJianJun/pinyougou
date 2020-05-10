 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	$scope.queyByParentId=function (Id) {
		itemCatService.queyByParentId(Id).success(
			function (response) {
				$scope.list=response;
            }
		);
    }

    // $scope.queryById=function (parentId) {
		// itemCatService.queryById(parentId).success(
		// 	function (response) {
		// 		$scope.list=response;
    //         }
		// )
    // }

    $scope.grade=1;

	$scope.setGrade=function (value) {
		$scope.grade=value;
    }

    // $scope.modifyGrade=function (value) {
		// $scope.grade=value;
		// alert($scope.grade);
    // }

    $scope.selectList=function (h) {

		alert(h);


		if ( $scope.grade==1) {
			$scope.Dad=null;
			$scope.Son=null;
		}
		if ( $scope.grade==2) {
			$scope.Dad=h;
			$scope.Son=null;
		}
		if ( $scope.grade==3) {
			$scope.Son=h;
		}
		alert($scope.Dad.name);
		alert($scope.Dad);
		$scope.queyByParentId(h.id);
    }
    
});	
