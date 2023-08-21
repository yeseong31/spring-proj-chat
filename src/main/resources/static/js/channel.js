$(document).ready(function () {
  $("a[class=btnChannelName]").click(function () {
    var index = $(this).attr("id");
    var channelId = $('.tdChannelId').eq(index).text();
    $("#inputChannelId").val(channelId);
  });
});