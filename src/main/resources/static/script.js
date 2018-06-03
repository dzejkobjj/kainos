var from, to, chart, fromPicker, toPicker, maxDate, currentData, numberOfTrendLines;
var dateFormat = "yy-mm-dd";

$(document).ready(function() {
    var date = new Date();
    date.setDate(date.getDate() - 1);
    to = formatDate(date);
    maxDate = to;
    date.setMonth(date.getMonth() - 1);
    from = formatDate(date);
    initialize();
    getDataAndMakeChart(from,to);

});
function formatDate(date) {
    var day = date.getDate();

    if(day<10)
        day = "0" + day;
    var month = date.getMonth();
    month++;
    if(month<10)
        month = "0"+month;

    return date.getFullYear() + "-" + month + "-" + day;
}

function getDataAndMakeChart(from, to) {
    $.ajax({
        url:"/api?from=" + from + "&to=" + to,
        type: "get",
        dataType : "json",
        success :function (response) {
            currentData = response;
            makeNewChart(response);
            return response;
        },
        error: function () {
            console.log("error");
        }});
}


function makeNewChart(data) {
    $("#chart").empty();

    chart = new Highcharts.chart('chart', {

        title: {
            text: 'Cryptocurrency Value Chart'
        },

        xAxis: {
            tickmarkPlacement: 'on',
            title: {
                enabled: false
            },
            tiickInterval: 1, //force to display categories one by one
            labels: {
                formatter: function(){
                    return data.dates[this.value];
                }
            }
        },

        yAxis: {
            type: 'logarithmic',
            minorTickInterval: 0.1
        },

        tooltip: {
            headerFormat: '<b>{series.name}</b><br />',
            pointFormat: 'Date = {point.x}, Value = {point.y}'
        },

        series: [data.bitcoin, data.ethereum, data.litcoin]
    });

    calculateTrends();
}

function addTrendLine(sourceData, offset) {
    chart.addSeries({
        enableMouseTracking:false,
        pointStart: offset,
        type: 'line',
        marker: { enabled: false },
        /* function returns data for trend-line */
        data: (function() {
            return fitOneDimensionalData(sourceData);
        })()
    });
}
function initialize() {
    $( "#fromPicker" ).val(from);
    $( "#toPicker" ).val(to);
    fromPicker = $( "#fromPicker" )
            .datepicker({
                dateFormat: dateFormat,
                defaultDate: from,
                changeMonth: true,
                numberOfMonths: 1,
                maxDate:maxDate
            })
            .on( "change", function() {
                from = this.value;
                toPicker.datepicker( "option", "minDate", getDate(this) );
                getDataAndMakeChart(from,to);
            });
    toPicker = $( "#toPicker" ).datepicker({
            dateFormat: dateFormat,
            defaultDate: to,
            changeMonth: true,
            numberOfMonths: 1,
            maxDate: maxDate
        })
            .on( "change", function() {
                to = this.value;
                getDataAndMakeChart(from,to);
            });

}


function getDate( element ) {
    var date;
    try {
        date = $.datepicker.parseDate( dateFormat, element.value );
    } catch( error ) {
        date = null;
    }

    return date;
}

function findMin(data) {
    var min;
    for(var i=0;i<data.length;i++){
        min = data[i].bitcoin<data[i].ethereum ? data[i].bitcoin : data[i].ethereum;
        min = min < data[i].litcoin ? min : data[i].litcoin;
    }
    return min;
}

function fitOneDimensionalData(source_data) {
    var trend_source_data = [];
    for(var i = source_data.length; i-->0;) {
        trend_source_data[i] = [i, source_data[i]]
    }
    var regression_data = fitData(trend_source_data).data
    var trend_line_data = [];
    for(var i = regression_data.length; i-->0;) {
        trend_line_data[i] = regression_data[i][1];
    }
    return trend_line_data;
}

//dodajemy linie trendu w zaleznosci od ilosci wybranych dni, jesli 30 lub wiecej to obliczamy miesieczny trend, jesli mniej to tygodniowy
function calculateTrends() {
    if ($("#showTrendline").is(':checked')) {
        var oneDay = 24*60*60*1000;
        var splited = from.split("-");
        var fromDate = new Date(splited[0], splited[1], splited[2]);
        splited = to.split("-");
        var toDate = new Date(splited[0], splited[1], splited[2]);

        var diffDays = Math.round(Math.abs((fromDate.getTime() - toDate.getTime()) / (oneDay)));

        if (diffDays >= 30) {
            addTrendlinesDependentOnDays(30, diffDays);
        }else{
            addTrendlinesDependentOnDays(7, diffDays);
        }
    }else{
        if(numberOfTrendLines > 0){
            var k = 2 + numberOfTrendLines;
            for(var i=3; i<=k;i++){
                chart.series[i].remove();
            }
        }
    }
}

function addTrendlinesDependentOnDays(days, diffDays) {
    numberOfTrendLines = Math.round(diffDays / days);
    var btcData = currentData.bitcoin.data;

    var k=1;
    for (var i = 0; i < numberOfTrendLines;i++) {
        addTrendLine(btcData.slice(i*days, k * days), i*days);
        k++;
    }
}

// function chunkArray(myArray, chunk_size){
//     var index = 0;
//     var arrayLength = myArray.length;
//     var tempArray = [];
//
//     for (index = 0; index < arrayLength; index += chunk_size) {
//         myChunk = myArray.slice(index, index+chunk_size);
//         // Do something if you want with the group
//         tempArray.push(myChunk);
//     }
//
//     return tempArray;
// }