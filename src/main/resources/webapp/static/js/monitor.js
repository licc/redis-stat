$(function () {
    // 基于准备好的dom，初始化echarts实例

    var charts_clients = [];
    var charts_blocked_clients = [];
    var charts_tps = [];

    var input_kbps = [];
    var output_kbps = [];


    var charts_tps_chart = echarts.init(document.getElementById('charts_tps'));
    var charts_clients_chart = echarts.init(document.getElementById('charts_clients'));
    var charts_io_chart = echarts.init(document.getElementById('charts_io'));


    var optionTps = {

        tooltip: {
            trigger: 'axis',
            formatter: function (params) {
                var result = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', params[0].value[0], false);
                params.forEach(function (item) {
                    result += '<br/>';
                    result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
                    result += item.seriesName + "：";
                });

                return result + params[0].value[1];
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

        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
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

            dataZoom: [
                {
                    start: 95,
                    end: 100
                }
            ],
            data: []
        }]
    };

    var optionIO = {

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

        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
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
            dataZoom: [
                {
                    start: 95,
                    end: 100
                }
            ],
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
        }
        charts_tps_chart.setOption({
            title: {

                text: '执行命令数'
            },

            series: [{
                type: 'line',
                name: 'TPS',

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

                data: charts_clients
            }
                , {
                    name: '阻塞Client',
                    type: 'line',
                    data: charts_blocked_clients
                }
            ]
        });

        charts_io_chart.setOption({
            legend: {
                data: ['input_kbps', 'output_kbps']
            },

            title: {
                text: '网络流量-Redis3.0'
            },

            series: [{
                name: 'input_kbps',
                type: 'line',

                data: input_kbps
            }
                , {
                    name: 'output_kbps',
                    type: 'line',
                    data: output_kbps
                }
            ]
        });

    }


    var stompClient = null;


    function connect(event) {

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
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
        console.log(error)
    }

    function dataFormat(value) {
        now = new Date();
        nowStr = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', now, false);
        return {
            name: nowStr,
            value: [
                nowStr,
                parseInt(value ? value : '0')
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


        charts_tps.push(dataFormat(data.instantaneous_ops_per_sec));
        charts_clients.push(dataFormat(data.connected_clients));
        charts_blocked_clients.push(dataFormat(data.blocked_clients));
        input_kbps.push(dataFormat(data.instantaneous_input_kbps));
        output_kbps.push(dataFormat(data.instantaneous_output_kbps));

        initText(data);

        // onDataRefresh();
    }

    connect();

    charts_tps_chart.setOption(optionTps);
    charts_clients_chart.setOption(optionClients);
    charts_io_chart.setOption(optionIO);
    window.addEventListener("resize", function () {
        charts_tps_chart.resize();
        charts_clients_chart.resize();
        charts_io_chart.resize();
    });
});
