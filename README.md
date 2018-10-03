# LiveStream
https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveBroadcasts.insert?part=id%252Csnippet%252CcontentDetails%252Cstatus&_h=1&resource=%257B%250A++%2522snippet%2522%253A+%250A++%257B%250A++++%2522scheduledStartTime%2522%253A+%25222018-10-03T20%253A20%253A00%252B03%253A00%2522%252C%250A++++%2522title%2522%253A+%2522hello2%2522%250A++%257D%252C%250A++%2522status%2522%253A+%250A++%257B%250A++++%2522privacyStatus%2522%253A+%2522public%2522%250A++%257D%250A%257D&

https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveStreams.insert?part=id%252Csnippet%252Ccdn%252Cstatus&_h=1&resource=%257B%250A++%2522snippet%2522%253A+%250A++%257B%250A++++%2522title%2522%253A+%2522hi%2522%250A++%257D%252C%250A++%2522cdn%2522%253A+%250A++%257B%250A++++%2522format%2522%253A+%25221080p%2522%252C%250A++++%2522ingestionType%2522%253A+%2522rtmp%2522%250A++%257D%250A%257D&

https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveBroadcasts.bind?id=Mnjv0fGbx6E&part=id%252Csnippet%252CcontentDetails%252Cstatus&streamId=ENp2jseLjEdhoteLLuknHw1538587984167798&_h=1&

https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveBroadcasts.list

https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveStreams.list?part=id%252Csnippet%252Ccdn%252Cstatus&id=ENp2jseLjEdhoteLLuknHw1538587051302969&_h=1&

https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.liveBroadcasts.transition?broadcastStatus=complete&id=Mnjv0fGbx6E&part=id%252Csnippet%252CcontentDetails%252Cstatus&_h=4&

https://developers.google.com/youtube/v3/live/docs/liveBroadcasts/transition#params

VBR="2500k"                                    # Bitrate de la vidéo en sortie
FPS="30"                                       # FPS de la vidéo en sortie
QUAL="medium"                                  # Preset de qualité FFMPEG
YOUTUBE_URL="rtmp://a.rtmp.youtube.com/live2"  # URL de base RTMP youtube

SOURCE="udp://239.255.139.0:1234"              # Source UDP (voir les annonces SAP)
KEY="...."                                     # Clé à récupérer sur l'event youtube

ffmpeg \
    -i "$SOURCE" -deinterlace \
    -vcodec libx264 -pix_fmt yuv420p -preset $QUAL -r $FPS -g $(($FPS * 2)) -b:v $VBR \
    -acodec libmp3lame -ar 44100 -threads 6 -qscale 3 -b:a 712000 -bufsize 512k \
    -f flv "$YOUTUBE_URL/$KEY"
    
    
    
   ffmpeg -i 355295.mp4 -deinterlace -vcodec libx264 -pix_fmt yuv420p -preset medium -r 30 -g 60 -b:v 2500k -acodec libmp3lame -ar 44100 -threads 6 -qscale 3 -b:a 712000 -bufsize 512k -f flv rtmp://a.rtmp.youtube.com/live2/bkj4-zk8s-5h14-4rag
    
    

Основной поток:
Видеопоток поступает слишком медленно. Просмотр может идти с буферизацией.
Основной поток:
Текущее разрешение (640x360) не поддерживается на YouTube. Рекомендуем использовать формат 1920 x 1080.
Основной поток:
Низкая скорость передачи данных. У зрителей могут возникнуть проблемы с буферизацией. Проверьте скорость соединения или используйте более низкий битрейт.
3 октября 2018 г., 10:38 (GMT-7)
Основной поток:
Видеопоток поступает слишком медленно. Просмотр может идти с буферизацией.
Основной поток:
Текущее разрешение (640x360) не поддерживается на YouTube. Рекомендуем использовать формат 1920 x 1080.
Основной поток:
Низкая скорость передачи данных. У зрителей могут возникнуть проблемы с буферизацией. Проверьте скорость соединения или используйте более низкий битрейт.
