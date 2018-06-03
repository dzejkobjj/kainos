var from, to, chart, fromPicker, toPicker, maxDate, currentData, numberOfTrendLines, minDate;
var dateFormat = "yy-mm-dd";

var trendDiff={
    bitcoin:[50,100],
    ethereum:[25,50],
    litcoin:[10,25]
}
$(document).ready(function() {
    var date = new Date();
    date.setDate(date.getDate() - 1);
    to = formatDate(date);
    maxDate = to;
    date.setMonth(date.getMonth() - 1);
    from = formatDate(date);
    getMinDate();
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

function getMinDate() {
    $.ajax({
        url:"/api/min",
        type: "get",
        dataType : "json",
        success :function (response) {
            minDate = response[0].minDate;
            console.log(minDate);
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
            pointFormat: 'Date = {data.dates[point.x]}, Value = {point.y}'
        },

        series: [data.bitcoin, data.ethereum, data.litcoin]
    });

    calculateTrends();
}

function addTrendLine(sourceData, offset, color) {
    chart.addSeries({
        enableMouseTracking:false,
        showInLegend: false,
        pointStart: offset,
        color: color,
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
                minDate: minDate,
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
    $("#trendArrowDiv").empty();
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
            for(var i=k; i>=3;i--){
                console.log(i);
                console.log(chart.series[i]);
                chart.series[i].remove();
            }
            numberOfTrendLines = 0;
        }
    }
}

//uznajemy ze jesli w roznica w cenie btc w przeciagu miesiaca wynosi 100 usd to jest trend rosnacy/malejacy, 50 usd dla tygodnia
function addTrendlinesDependentOnDays(days, diffDays) {
    numberOfTrendLines = Math.round(diffDays / days);
    var btcData = currentData.bitcoin.data;
    var color;
    if(days>=30)
        picker = 1;
    else
        picker = 0;
    var k=1;
    for (var i = 0; i < numberOfTrendLines;i++) {
        var temp = btcData.slice(i*days, k * days)
        if(temp[0] - temp[temp.length-1] > trendDiff.bitcoin[picker]){
            color = 'red';
            addTrendLine(temp, i*days, color);
        }
        else if(temp[temp.length-1] - temp[0] > trendDiff.bitcoin[picker]){
            color = 'green';
            addTrendLine(temp, i*days, color);
        }
        addTrendArrowBox(temp, i*days, k*days, picker);
        k++;
    }
}

function addTrendArrowBox(btcData, startIndex, stopIndex, picker) {
    var mother = $("#trendArrowDiv");
    var box, img, txt, header;
    if(btcData[0] - btcData[btcData.length-1] > trendDiff.bitcoin[picker]){
        var box = $('<div>').attr("class", "col");
        var img = $('<img>').attr("src", "arrowdownred.svg").attr("width", "100").attr("height", "100");
        var txt = $('<div>').append(checkOtherCurrencyGrowth(true, startIndex, stopIndex, picker) + "<br>" + checkOtherCurrencyGrowth(false, startIndex, stopIndex, picker));
        header = $('<b>').append("BTC is going down - " + currentData.dates[stopIndex]);
        mother.append(box.append(img, header, txt));

    }
    else if(btcData[btcData.length-1] - btcData[0] > trendDiff.bitcoin[picker]){
        var box = $('<div>').attr("class", "col arrow-box");
        var img = $('<img>').attr("src", "arrowupgreen.svg").attr("width", "100").attr("height", "100");
        var txt = $('<div>').append(checkOtherCurrencyGrowth(true, startIndex, stopIndex, picker) + "<br>" + checkOtherCurrencyGrowth(false, startIndex, stopIndex, picker));

        header = $('<b>').append("BTC is going up - " + currentData.dates[stopIndex]);
        mother.append(box.append(img, header, txt));
    }

}

function checkOtherCurrencyGrowth(isEth, startIndex, stopIndex, picker) {
    var i = 0;
    if(isEth){
        if(currentData.ethereum.data[startIndex] - currentData.ethereum.data[stopIndex] > trendDiff.ethereum[picker])
            return "Ethereum was going down at that time";
        else if (currentData.ethereum.data[stopIndex] - currentData.ethereum.data[startIndex] > trendDiff.ethereum[picker])
            return "Ethereum was going up at that time";
        else return "";
    }else{
        if(currentData.litcoin.data[startIndex] - currentData.litcoin.data[stopIndex] > trendDiff.ethereum[picker])
            return "Litcoin was going down at that time";
        else if (currentData.litcoin.data[stopIndex] - currentData.litcoin.data[startIndex] > trendDiff.ethereum[picker])
            return "Litcoin was going up at that time";
        else return "";
    }
    
}
// TODO strona sie rozjezdza gdy mamy za duzo szczalek trendu
