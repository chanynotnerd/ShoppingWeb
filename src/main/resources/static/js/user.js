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
		$("#btn-delete").on("click", () => {
		    _this.deleteUser();
		});
		$("#sample6_postcode").on("click", () => {
                    _this.execDaumPostcode();
        });
	},

    execDaumPostcode: function() {
            let _this = this;
            new daum.Postcode({
                oncomplete: function(data) {
                    document.getElementById('sample6_postcode').value = data.zonecode;
                    document.getElementById('sample6_address').value = data.address;
                    document.getElementById('sample6_detailAddress').value = data.buildingName;
                    // 검색창 닫기
                    _this.closeDaumPostcode();
                }
            }).open();
        },
        closeDaumPostcode: function() {
            // 팝업창 닫기
            let element_layer = document.getElementById('postcode-layer');
            element_layer.style.display = 'none';
        },

	insertUser: function() {
		alert("회원가입 요청됨");
		let user = {	// user 객체 선언
			username: $("#username").val(),
			password: $("#password").val(),
			email: $("#email").val(),
			postcode: $("#sample6_postcode").val(),
                        address: $("#sample6_address").val(),
                        detailAddress: $("#sample6_detailAddress").val()
		}
		if(user.password.trim() === "")
                {
                    alert("비밀번호를 입력해주세요.");
                    return;
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
				if (errors.email != null) warn = warn + errors.email + "\n";
				if (errors.postcode != null) warn = warn + errors.postcode + "\n";
				if (errors.address != null) warn = warn + errors.address + "\n";
				if (errors.detailAddress != null) warn = warn + errors.detailAddress;
				alert(warn);
			}

		}).fail(function(error) {
			alert("에러 발생 : " + error);
		});
	},

	updateUser: function() {
		let user = {	// user 객체 선언
		            id: $("#id").val(),
        			username: $("#username").val(),
        			password: $("#password").val(),
        			email: $("#email").val(),
        			postcode: $("#sample6_postcode").val(),
                                address: $("#sample6_address").val(),
                                detailAddress: $("#sample6_detailAddress").val()
        		}
        if(user.password.trim() === "")
                {
                    alert("비밀번호를 입력해주세요.");
                    return;
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
