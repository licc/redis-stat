// $.get("/monitor/" + node,
//     function (data) {
//     var now = new Date();
//     // var now = echarts.format.formatTime('yyyy-MM-dd hh:mm:ss', new Date(), true);
//
//     charts_tps.push([now, parseInt(data.instantaneous_ops_per_sec)]);
//     charts_clients.push([now, parseInt(data.connected_clients)]);
//     charts_blocked_clients.push([now, parseInt(data.blocked_clients)]);
//     input_kbps.push([now, parseInt(data.instantaneous_input_kbps ? data.instantaneous_input_kbps : 0)]);
//     output_kbps.push([now, parseInt(data.instantaneous_output_kbps ? data.instantaneous_output_kbps : 0)]);
//
//     if (charts_tps.length > 300) {
//         charts_tps.shift();
//         charts_clients.shift();
//     }
//     $("#dbsize").text(data.dbSize);
//     if (data.dbSize > 0) {
//         eval("var " + data.db0);
//         $("#dbsize_expired").text("(临时:" + expires + ")");
//     }
//     $("#used_memory_human").text(data.used_memory_human);
//     $("#blocked_clients").text(data.blocked_clients);
//     var hit_rate = data.keyspace_hits / (parseInt(data.keyspace_hits) + parseInt(data.keyspace_misses));
//     $("#hit_rate").text((hit_rate * 100).toFixed(1));
// });