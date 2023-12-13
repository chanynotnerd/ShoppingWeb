// userObject 객체 선언, let 키워드는 선언하는 키워드
let userObject =
{
	init: function()
	{
		let _this = this;

		$("#btn-save").on("click", () => {
			_this.insertUser();
		});
		$("#btn-update").on("click", () => {
			_this.updateUser();
		});
		$("#sample6_postcode").on("click", () => {
                    _this.execDaumPostcode();
        });
	},

    execDaumPostcode: function() {
            new daum.Postcode({
                oncomplete: function(data) {
                    document.getElementById('sample6_postcode').value = data.zonecode;
                    document.getElementById('sample6_address').value = data.address;
                    document.getElementById('sample6_detailAddress').value = data.buildingName;
                    document.getElementById('sample6_extraAddress').value = data.detailAddress;
                }
            }).open();
        },

	insertUser: function() {
		alert("회원가입 요청됨");
		let user = {	// user 객체 선언
			username: $("#username").val(),
			password: $("#password").val(),
			email: $("#email").val(),
			postcode: $("#sample6_postcode").val(),
                        address: $("#sample6_address").val(),
                        detailAddress: $("#sample6_detailAddress").val(),
		}
		$.ajax({
			type: "POST",
			url: "/auth/insertUser",
			data: JSON.stringify(user),
			contentType: "application/json; charset=utf-8"
		}).done(function(response) {
			let status = response["status"];
			if (status == 200) {
				let message = response["data"];
				alert(message);
				location = "/";
			}
			else {
				let warn = "";
				let errors = response["data"];
				if (errors.username != null) warn = warn + errors.username + "\n";
				if (errors.password != null) warn = warn + errors.password + "\n";
				if (errors.email != null) warn = warn + errors.email;
				alert(warn);
			}

		}).fail(function(error) {
			alert("에러 발생 : " + error);
		});
	},

	updateUser: function() {
		alert("회원 정보 수정 요청");
		let user = {
			id: $("#id").val(),
			username: $("#username").val(),
			password: $("#password").val(),
			email: $("#email").val()
		}

		$.ajax({
			type: "PUT",
			url: "/user",
			data: JSON.stringify(user),
			contentType: "application/json; charset=utf-8"
		}).done(function(response) {
            			let status = response["status"];
            				let message = response["data"];
            				alert(message);
            				location = "/";
		}).fail(function(error) {
			let message = error["data"];
			alert("에러 발생 : " + error);
		});
	},

}


userObject.init();
