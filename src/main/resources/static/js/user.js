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
		$("#btn-login").on("click", function (e) {
			e.preventDefault(); // 폼의 기본 제출 동작을 방지

			var username = $("#username").val();
			var password = $("#password").val();

			userObject.login(username, password); // 로그인 함수 호출
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

	login: function (username, password) {
		let _this = this;
		$.ajax({
			url: '/auth/login',
			type: 'POST',
			contentType: 'application/json',
			data: JSON.stringify({username: username, password: password}),
			success: function (response) {
				localStorage.setItem('jwtToken', response.token);
				_this.checkAuthStatusAndUpdateMenu(); // 메뉴 상태 업데이트
				/*location = "/";*/
			},
			error: function (xhr) {
				let errorMessage = xhr.status + ": " + (xhr.responseText || "로그인 실패");
				alert(errorMessage);
			}
		});
	},

	// 로그아웃 함수
	logout: function () {
		localStorage.removeItem('jwtToken');
		this.checkAuthStatusAndUpdateMenu();
		window.location.href = '/auth/login'; // 로그인 페이지로 리다이렉트
	},

	// 인증 상태 확인 및 메뉴 업데이트 함수
	checkAuthStatusAndUpdateMenu: function () {
		let token = localStorage.getItem('jwtToken');
		if (token) {
			try {
				let payload = JSON.parse(atob(token.split('.')[1]));
				let isAdmin = payload.authority.includes('ADMIN');
				this.updateMenu(true, isAdmin);
			} catch (e) {
				console.error('Error decoding token', e);
				this.updateMenu(false, false);
			}
		} else {
			this.updateMenu(false, false);
		}
	},

	// 메뉴 업데이트 함수
	updateMenu: function (isLoggedIn, isAdmin) {
		// 로그인 관련 UI를 숨기거나 보여줄 요소들
		let loginUI = $("#non-authenticated-ui");
		let logoutUI = $("#authenticated-ui");
		let adminUI = $("#admin-ui");

		if (isLoggedIn) {
			loginUI.hide(); // 로그인 메뉴 숨기기
			logoutUI.show(); // 로그아웃 메뉴 보이기

			if (isAdmin) {
				adminUI.show(); // 관리자 메뉴 보이기
			} else {
				adminUI.hide(); // 관리자 메뉴 숨기기
			}
		} else {
			logoutUI.hide(); // 로그아웃 메뉴 숨기기
			loginUI.show(); // 로그인 메뉴 보이기
			adminUI.hide(); // 관리자 메뉴 숨기기
		}
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
