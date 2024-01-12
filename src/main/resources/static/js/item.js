let itemObject =
    {
        init: function () {
            let _this = this

            $(document).ready(function () {
                _this.fetchUserInfoFromIndex();
            });
        },

        fetchUserInfoFromIndex: function () {
            let token = localStorage.getItem('accessToken');
            console.log('Token from storage:', token);
            if (!token) {
                console.error('No access token available.');
                // 토큰이 없으면 로그인 페이지로 리다이렉트
                /*window.location.href = '/auth/login';*/
                return;
            }
            $.ajax({
                url: '/', // 사용자 정보를 가져오는 서버의 엔드포인트
                type: 'GET',
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + token);
                },
                success: function (response) {
                    /*console.log(response);*/
                    var id = localStorage.getItem('id');
                    var username = localStorage.getItem('username');
                    var email = localStorage.getItem('email');
                    var postcode = localStorage.getItem('postcode');
                    var address = localStorage.getItem('address');
                    var detailAddress = localStorage.getItem('detailAddress');

                    /*$('#userId').val(response.id);*/
                    /*$('#usernameInput').val(response.username);
                    $('#emailInput').val(response.email);*/
                    $('#userId').val(id);
                    $('#usernameInput').val(username);
                    $('#emailInput').val(email);
                    $('#postcodeInput').val(postcode);
                    $('#addressInput').val(address);
                    $('#detailAddressInput').val(detailAddress);
                },
                error: function (error) {
                    console.error("Error fetching user info: ", error);
                    if (error.status === 401) { // 인증 실패시
                        window.location.href = '/auth/login';
                    }
                }
            });

        }
    }
itemObject.init();