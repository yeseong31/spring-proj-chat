$(document).ready(function () {
  $("a[class=btnChannelName]").click(function () {
    var index = $(this).attr("id");
    var channelUuid = $('.tdChannelUuid').eq(index).text();
    $("#inputChannelUuid").val(channelUuid);
  });
});