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
        		let cart = {	// user 객체 선언
                    userId: userId,
                    itemId: itemId,
        			amount: $("#amount").val()
        		}

				$.ajax({
					type: "POST",
					url: "/cart/user/" + userId + "/" + itemId,
					data: JSON.stringify(cart),
					contentType: "application/json; charset=utf-8"
				}).done(function(response) {
					let status = response["status"];
					if (status == 200) {
						let message = response["data"];
						alert(message);
						window.location.href = "/item/" + itemId;
					}
					else {
						let warn = "";
						let errors = response["data"];
						if (errors.userId != null) warn = warn + errors.userId + "\n";
						if (errors.itemId != null) warn = warn + errors.itemId + "\n";
						if (errors.amount != null) warn = warn + errors.amount;
						alert(warn);
					}

				}).fail(function(error) {
					alert("에러 발생 : " + error);
				});
	}
}

cartObject.init();