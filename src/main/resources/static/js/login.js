let loginObject =
{
	init: function()	// init: 함수는 userObject 객체가 초기화되어 처음 사용될 준비가 될 때 호출되는 함수,
	{
		// #btn-save 버튼에 click 이벤트가 발생하면 insertUser() 함수 호출
		$("#btn-login").on("click", () => {	// jQuery를 사용하여 "#btn-save"라는 HTML 요소에 클릭 이벤트 설정
			this.login();		// 클릭되면 insertUser 함수 호출.
		});
	},

    login : function() {
    alert("로그인 요청됨");
    		let data = {	// user 객체 선언
    			username: $("#username").val(),	// $().val() 메소드는 선택한 HTML 요소의 값을 반환하는 jQuery 메소드
    			password: $("#password").val()	// #username, #password, #email 전부 각각 해당 id를 가진
    		}

            $.ajax({
            			type: "POST",	// 요청 방식
            			url: "/auth/login",	// 요청 경로
            			data: JSON.stringify(data),	// user 객체를 JSON 형식으로 변환
            			// HTTP의 body에 설정되는 데이터 마임타임
            			contentType: "application/json; charset=utf-8"
            			// 응답으로 들어온 JSON 데이터를 response로 받는다.
            		}).done(function(response) {
            			// 응답 메세지를 콘솔에 출력하고 메인 페이지로 이동
            			let message = response["data"];
                        alert(message);
                        location = "/";
            		}).fail(function(error) {
            			// 에러 메세지를 알림창에 출력
            			let message = error["data"];
            			alert("에러 발생 : " + error);
            		});
    },
}
loginObject.init();