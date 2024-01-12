let cartObject =
{
	init: function()
	{
		let _this = this;

		$("#btn-save-item").on("click", () => {
			_this.addToCart();
		});
        // $(document)로 생성되는 모든 버튼에 대해 이벤트 적용
        $(document).on('click', "#btn-delete", (e) => {
        			let itemId = $(e.target).data('cart-item-id');
        			_this.deleteItem(itemId);
        });
        $(document).on("click", "#btn-update", (e) => {
        /*let userId = $('body').data('user-id');
            let itemId = $(e.target).data('cart-item-id');
            let count = Number($("#count" + itemId).val()); // count 값 확인*/
                _this.updateCount(e);
        });
        /*$(document).on('click', '#btn-order', function() {
            let total = $('#totalAmount').text();
            let userId = window.userId;

            let orderData = {
                userId: userId,
                total: total,
            };
            _this.placeOrder(orderData);
        };*/
	},

	addToCart: function() {
		alert("장바구니에 제품이 담겼습니다.");
		var id = localStorage.getItem('id');
        		let cart = {	// user 객체 선언
                    userId: userId,
                    itemId: itemId,
        			amount: $("#amount").val()
        		}
		console.log(userId);
		console.log(itemId);
		/*console.log(amount);*/
		let token = localStorage.getItem('accessToken'); // 로컬 스토리지에서 토큰 가져옴.
				$.ajax({
					type: "POST",
					url: "/cart/add",
					headers: {
						'Authorization': 'Bearer ' + token // 토큰을 요청 헤더에 추가
					},
					data: JSON.stringify(cart),
					contentType: "application/json; charset=utf-8"
				}).done(function(response) {
				/*console.log(response);
				console.log("Request URL:", url);*/
					// let status = response["status"];
					if (response.status == 200) {
						let message = response["data"];
						alert(message);
						// window.location.href = "/item/" + itemId;
						location = "/item/" + itemId;
						location.reload(true);
						console.log(response);
					}
					else {
					alert("문제가 있습니다.");
						if (response.data != null) {
						let warn = "";
						let errors = response["data"];
						if (errors.userId != null) warn = warn + errors.userId + "\n";
						if (errors.itemId != null) warn = warn + errors.itemId + "\n";
						if (errors.amount != null) warn = warn + errors.amount;
						alert(warn);
					} else {
							console.log("data 값이 없는디용?");
						}
					}
				}).fail(function(error) {
					alert("에러 발생 : " + error);
					console.log(error);
				});
	},
	updateCount: function(e) {
	    let userId = window.userId;
    	alert("수량변경요청.");
        let itemId = $(e.target).data('cart-item-id');
        console.log(itemId);  // itemId 값 확인
        let amount = Number($("#count" + itemId).val()) || null;
        console.log(amount);
    	let cart = {	// cart 객체 선언
                        userId: userId,
                        itemId: itemId,
                	    amount: amount
                	}
        	$.ajax({
        		type: "PATCH", // 또는 "PATCH"
        		url: `/cart/updateCount`,
        		data: JSON.stringify(cart),
        		contentType: "application/json; charset=utf-8"
        	}).done(function(response) {
        		if (response.status == 200) {
        			location.reload(true);
        		}
        		else {
        			alert("문제가 있습니다.");
        		}
        	}).fail(function(error) {
        		alert("에러 발생 : " + error);
        		console.log(error);
        	});
        },
	deleteItem : function(itemId) {
        $.ajax({
        			type: "DELETE",
        			url: `/cart/item`,
        			data:JSON.stringify({itemId}),
        			contentType: "application/json; charset=utf-8"
        				}).done(function(response) {
        				/*console.log(response);
        				console.log("Request URL:", url);*/
        					// let status = response["status"];
        					if (response.status == 200) {
        						let message = response["data"];
        						alert(message);
        						location.reload(true);
        					}
        					else {
        					alert("문제가 있습니다.");
        						let warn = "";
        						let errors = response["data"];
        						if (errors.userId != null) warn = warn + errors.userId + "\n";
        						if (errors.itemId != null) warn = warn + errors.itemId + "\n";
        						if (errors.amount != null) warn = warn + errors.amount;
        						alert(warn);
        					}

        				}).fail(function(error) {
        					alert("에러 발생 : " + error);
        					console.log(error);
        				});
	}
	/*placeOrder: function(orderData) {
            $.ajax({
                type: "POST",
                url: "/order/place",
                data: JSON.stringify(orderData),
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                location = "/";
            }).fail(function(error) {
                alert("에러 발생 : " + error);
                console.log(error);
            });
        }*/
}

cartObject.init();