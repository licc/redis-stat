var delId = ''
var Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000
});
$(function () {


    $('#addClusterForm').validate({
        submitHandler: function () {
            $.ajax({
                async: false,
                type: "POST",
                url: '/clusters/add',
                contentType: "application/json ; charset=utf-8",
                data: JSON.stringify($("#addClusterForm").serializeJSON()),
                dataType: "json",
                success: function (result) {
                    if (result.code == 0) {
                        window.location.reload()
                    }else {
                          $('#addClusterModal').modal('hide');
                            Toast.fire({
                                icon: 'warning',
                                title: '添加集群失败'+result.message,
                            })

                    }
                },
                error: function () {
                }
            })
        },
        rules: {
            name: {
                required: true,
            },
        },
        messages: {
            name: {
                required: "请输入集群名称",
            },
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.closest('.form-group').append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
        }
    });

    $('#addNodeForm').validate({
        submitHandler: function () {
            $.ajax({
                async: false,
                type: "POST",
                url: '/node/add',
                contentType: "application/json ; charset=utf-8",
                data: JSON.stringify($("#addNodeForm").serializeJSON()),
                dataType: "json",
                success: function (result) {
                    if (result.code == 0) {
                        window.location.reload()
                    }else {
                        $('#addNodeModal').modal('hide');

                        Toast.fire({
                                icon: 'warning',
                                title: '添加实例失败'+result.message,
                            })
                    }
                },
                error: function () {
                }
            })
        },
        rules: {
            host: {
                required: true,
            },
            port: {
                required: true,
            },
        },
        messages: {
            host: {
                required: "请输入实例地址",
            },
            port: {
                required: "请输入实例端口",
            },
        },
        errorElement: 'span',
        errorPlacement: function (error, element) {
            error.addClass('invalid-feedback');
            element.closest('.form-group').append(error);
        },
        highlight: function (element, errorClass, validClass) {
            $(element).addClass('is-invalid');
        },
        unhighlight: function (element, errorClass, validClass) {
            $(element).removeClass('is-invalid');
        }
    });


});


function confirmDeleteRemoteData(type) {
    $.ajax({
        url: '/' + type + "/" + delId,
        type: 'DELETE',
        success: function (result) {
            if (result.code == 0) {
                window.location.reload()
            }else {
                if(type=='node'){
                    $('#del-node-confirm').modal('hide');
                    Toast.fire({
                        icon: 'warning',
                        title: '删除实例失败',
                    });
                }else {
                    $('#del-cluster-confirm').modal('hide');
                    Toast.fire({
                        icon: 'warning',
                        title: '删除集群失败',
                    });

                }

            }
        }
    });

}

function values(type, id) {
    $("#addClusterForm").children().find("input[name = 'name']").val('');

    if (type == 'node') {
        $("#addClusterForm").children().find("input[name = 'host']").val('');
        $("#addClusterForm").children().find("input[name = 'port']").val('');
        $("#addNodeForm").children().find("input[name = 'clusterId']").val(id);

    }
    if (type = 'del_cluster') {
        delId = id

    }
    if (type = 'del_node') {
        delId = id
    }

}
