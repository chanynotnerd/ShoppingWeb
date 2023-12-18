let cartObject =
{
	init: function()
	{
		let _this = this;

		$("#btn-save").on("click", () => {
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
        })
	},

	addToCart: function() {
		        alert("장바구니에 제품이 담겼습니다.");
        		let cart = {	// user 객체 선언
                    userId: userId,
                    itemId: itemId,
        			amount: $("#amount").val()
        		}

				$.ajax({
					type: "POST",
					url: "/cart/add",
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
}

cartObject.init();