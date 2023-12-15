let cartObject =
{
	init: function()
	{
		let _this = this;

		$("#btn-save").on("click", () => {
			_this.addToCart();
		});
	},

	addToCart: function() {
		        alert("장바구니에 제품이 담겼습니다.");
		        /*let form = $("form"); // form 요소 선택
                let url = form.attr('action'); // form의 action 속성 사용*/
        		let cart = {	// user 객체 선언
                    userId: userId,
                    itemId: itemId,
        			amount: $("#amount").val()
        		}
        		console.log(cart);

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
						location = "/";
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
	}
}

cartObject.init();