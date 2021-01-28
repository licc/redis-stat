$(function () {
    // 基于准备好的dom，初始化echarts实例

    var charts_clients = [];
    var charts_blocked_clients = [];
    var charts_tps = [];

    var input_kbps = [];
    var output_kbps = [];
    var cpu_sys_children = [];
    var cpu_usr_children = [];
    var cpu_sys_main_thr = [];
    var cpu_usr_main_thr = [];


    var charts_tps_chart = echarts.init(document.getElementById('charts_tps'));
    var charts_clients_chart = echarts.init(document.getElementById('charts_clients'));
    var charts_io_chart = echarts.init(document.getElementById('charts_io'));
    var charts_cpu_chart = echarts.init(document.getElementById('charts_cpu'));


    var optionClients = {
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var result = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', params[0].value[0], false);
                params.forEach(function (item) {
                    result += '<br/>';
                    result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
                    result += item.seriesName + "：" + item.value[1];
                });

                return result;
            },
            axisPointer: {
                animation: false
            }
        },

        xAxis: {
            type: 'time',
            splitLine: {
                show: false
            },
            interval: 10000,
            axisLabel: {
                formatter: function (value, index) {
                    var lab = echarts.format.formatTime('hh:mm:ss', value, false);
                    return lab;//这里写处理逻辑
                },
                interval: 10000,

                rotate: 40,//斜体展示

            }
        },
        yAxis: {
            type: 'value',
            minInterval: 1,
            minorSplitLine: {
                show: true
            },
            minorTick: {
                show: true
            }
        },

        series: [{
            type: 'line',

            showSymbol: false,
            hoverAnimation: false,

            data: []
        }]
    };


    var cpuOptionClients = {
        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var result = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', params[0].value[0], false);
                params.forEach(function (item) {
                    result += '<br/>';
                    result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
                    result += item.seriesName + "：" + item.value[1];
                });

                return result;
            },
            axisPointer: {
                animation: false
            }
        },

        xAxis: {
            type: 'time',
            splitLine: {
                show: false
            },
            interval: 10000,
            axisLabel: {
                formatter: function (value, index) {
                    var lab = echarts.format.formatTime('hh:mm:ss', value, false);
                    return lab;//这里写处理逻辑
                },
                interval: 10000,

                rotate: 40,//斜体展示

            }
        },
        yAxis: {
            type: 'value',
            minInterval: 0.1,

            minorSplitLine: {
                show: true
            },
            minorTick: {
                show: true
            }
        },

        series: [{
            type: 'line',

            showSymbol: false,
            hoverAnimation: false,

            data: []
        }]
    };


    setInterval(function () {
            onDataRefresh()
        }
        , 1000);

    function onDataRefresh() {

        if (charts_tps.length > 180) {
            charts_tps.shift();
            charts_clients.shift();
            charts_blocked_clients.shift();
            input_kbps.shift();
            output_kbps.shift();
            cpu_sys_children.shift();
            cpu_usr_children.shift();
            cpu_sys_main_thr.shift();
            cpu_usr_main_thr.shift();
        }
        charts_tps_chart.setOption({
            title: {

                text: '执行命令数'
            },

            series: [{
                type: 'line',
                name: 'TPS',
                showSymbol: false,

                data: charts_tps
            }]
        });

        charts_clients_chart.setOption({
            legend: {
                data: ['连接Client', '阻塞Client']
            },

            title: {
                text: 'Client数量'
            },

            series: [{
                name: '连接Client',
                type: 'line',
                showSymbol: false,

                data: charts_clients
            }
                , {
                    name: '阻塞Client',
                    type: 'line',
                    showSymbol: false,

                    data: charts_blocked_clients
                }
            ]
        });

        charts_io_chart.setOption({
            legend: {
                data: ['output_kbps', 'input_kbps']
            },

            title: {
                text: '网络流量'
            },

            series: [
                {
                    name: 'output_kbps',
                    type: 'line',
                    areaStyle: {},
                    showSymbol: false,

                    data: output_kbps
                }, {
                    name: 'input_kbps',
                    type: 'line',
                    areaStyle: {},
                    showSymbol: false,

                    data: input_kbps
                }
            ]
        });
        charts_cpu_chart.setOption({
            legend: {
                data: ['系统CPU', '用户CPU', '进程CPU(sys)', '进程CPU(usr)']
            },

            title: {
                text: 'CPU时间(AVG)'
            },

            series: [{
                name: '系统CPU',
                type: 'line',
                showSymbol: false,

                data: cpu_sys_children
            }
                , {
                    name: '用户CPU',
                    type: 'line',
                    showSymbol: false,

                    data: cpu_usr_children
                }
                , {
                    name: '进程CPU(sys)',
                    type: 'line',
                    showSymbol: false,

                    data: cpu_sys_main_thr
                }
                , {
                    name: '进程CPU(usr)',
                    type: 'line',
                    showSymbol: false,

                    data: cpu_usr_main_thr
                }
            ]
        });

    }


    var stompClient = null;

    function connect() {

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.heartbeat.outgoing = 20000;
        stompClient.heartbeat.incoming = 0; //客户端不从服务端接收心跳包
        var headers = {
            node: node,
        };
        stompClient.connect(headers, onConnected, onError);
        stompClient.debug = null


    }


    function onConnected() {
        stompClient.subscribe('/topic/' + node, onMessageReceived);
    }

    function onError(error) {
        console.log("onError重连",error)
        //重连
        setTimeout(function () {
            connect();
        }, 5000)

    }

    function dataFormat(value, timeStr, type) {
        var n = type == 2 ? parseFloat(value).toFixed(2) : parseInt(value ? value : '0');
        now = new Date(parseInt(timeStr) * 1000);
        nowStr = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', now, false);
        return {
            name: nowStr,
            value: [
                nowStr, n
            ]
        }
    }

    function initText(data) {
        $("#dbsize").text(data.dbSize);
        $("#dbsize_expired").text("(限时:" + data.limitExpiredKeys + ")");

        $("#used_memory_human").text(data.used_memory_human);
        $("#blocked_clients").text(data.blocked_clients);
        var hit_rate = data.keyspace_hits / (parseInt(data.keyspace_hits) + parseInt(data.keyspace_misses));
        $("#hit_rate").text((hit_rate * 100).toFixed(1));
    }

    function onMessageReceived(payload) {
        var message = JSON.parse(payload.body);
        var data = message.data;

        charts_tps.push(dataFormat(data.instantaneous_ops_per_sec, data.sysTime));
        charts_clients.push(dataFormat(data.connected_clients, data.sysTime));
        charts_blocked_clients.push(dataFormat(data.blocked_clients, data.sysTime));
        input_kbps.push(dataFormat(data.instantaneous_input_kbps, data.sysTime, 2));
        output_kbps.push(dataFormat(data.instantaneous_output_kbps, data.sysTime, 2));
        cpu_sys_children.push(dataFormat(data.used_cpu_sys_rate, data.sysTime, 2));
        cpu_usr_children.push(dataFormat(data.used_cpu_user_rate, data.sysTime, 2));
        cpu_sys_main_thr.push(dataFormat(data.used_cpu_sys_children_rate, data.sysTime, 2));
        cpu_usr_main_thr.push(dataFormat(data.used_cpu_user_children_rate, data.sysTime, 2));

        initText(data);

    }

    connect();

    charts_tps_chart.setOption(optionClients);
    charts_clients_chart.setOption(optionClients);
    charts_io_chart.setOption(optionClients);
    charts_cpu_chart.setOption(cpuOptionClients);

    window.addEventListener("resize", function () {
        charts_tps_chart.resize();
        charts_clients_chart.resize();
        charts_io_chart.resize();
        charts_cpu_chart.resize();
    });
});
