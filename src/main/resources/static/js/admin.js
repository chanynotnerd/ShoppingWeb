let adminObject = {
    init: function() {
        let _this = this;

        $("#btn-update").on("click", (event) => {
            event.preventDefault();
            _this.updateUser();
        });
        $(".btn-delete").on("click", function(event) {
            event.preventDefault();
            let userId = $(this).data('user-delete-id');
            _this.deleteUser(userId);
        });
         $(".btn-update-item").on("click", function(event) {
            event.preventDefault();
            let itemId = $(this).data('item-update-id');
            _this.updateItem(itemId);
         });
         $(".btn-delete-item").on("click", function(event) {
            event.preventDefault();
            let itemId = $(this).data('item-delete-id');
            _this.deleteItem(itemId);
         });
    },

    updateUser: function() {
        let user = { // user 객체 선언
            id: $("#id").val(),
            username: $("#username").val(),
            password: $("#password").val(),
            email: $("#email").val(),
            authority: {
                          authorityName: $("#authorityName").val()
            }
        }
        console.log(user);
        console.log($("#authorityName").val());

        $.ajax({
            type: "PUT",
            url: "/admin/usermanage/" + user.id,
            data: JSON.stringify(user),
            contentType: "application/json; charset=utf-8"
        }).done(function(response) {
            let status = response["status"];
            if (status === 200) {
                let message = response["data"];
                alert(message);
                location.href = "/admin/usermanage";
            } else {
                let warn = "";
                let errors = response["data"];
                if (errors.username != null) warn += errors.username + "\n";
                if (errors.password != null) warn += errors.password + "\n";
                if (errors.email != null) warn += errors.email + "\n";
                if (errors.role != null) warn += errors.role;
                alert(warn);
            }
        }).fail(function(error) {
            alert("에러 발생 : " + error);
        });
    },
    deleteUser: function(userId) {
            $.ajax({
                type: "DELETE",
                url: "/admin/usermanage/" + userId,
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                alert("유저가 성공적으로 삭제되었습니다.");
                location.href = "/admin/usermanage";
            }).fail(function(error) {
                alert("에러 발생 : " + error);
            });
        },

    updateItem: function() {
            let item = { // item 객체 선언
                id: $("#id").val(),
                itemName: $("#itemName").val(),
                price: $("#price").val(),
                discountPercent: $("#discountPercent").val(),
                discountPrice: $("#discountPrice").val(),
                explaination: $("#explaination").val(),
                category: $("#category").val()
            }

            $.ajax({
                type: "PUT",
                url: "/admin/itemmanage/" + item.id,
                data: JSON.stringify(item),
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                if (response.ok) {
                    alert('아이템 정보가 성공적으로 수정되었습니다.');
                    location.href = "/";
                } else {
                    let warn = "";
                    let errors = response;
                    for (let error in errors) {
                        if (errors.hasOwnProperty(error)) {
                            warn += errors[error] + "\n";
                        }
                    }
                    alert(warn);
                }
            }).fail(function(error) {
                alert("에러 발생 : " + error);
            });
        },
    deleteItem: function(itemId) {
            $.ajax({
                type: "DELETE",
                url: "/api/v1/items/" + itemId,
                contentType: "application/json; charset=utf-8"
            }).done(function(response) {
                if (response.ok) {
                    alert('아이템이 성공적으로 삭제되었습니다.');
                    location.href = "/";
                } else {
                    alert("에러 발생 : " + response);
                }
            }).fail(function(error) {
                alert("에러 발생 : " + error);
            });
        }
}

adminObject.init();