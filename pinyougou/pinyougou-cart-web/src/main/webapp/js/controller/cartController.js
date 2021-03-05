app.controller("cartController", function ($scope, cartService) {

    $scope.getUsername = function(){
        cartService.getUsername().success(function (response) {
            $scope.username= response.username;

        });
    };

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            //统计总数量和总价
            $scope.totalValue = cartService.sumTotalValue(response);
        });

    };

    //加入购物车（增减购物车购买商品数量）
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId, num).success(function (response) {
            if (response) {
                //刷新列表
                $scope.findCartList();
            } else {
                alert("操作购物车失败！");
            }

        });

    };
});