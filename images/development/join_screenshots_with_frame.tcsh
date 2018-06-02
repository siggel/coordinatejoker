#!/usr/bin/tcsh

cd .

set files = `ls screenshots/de/*.png`
foreach file ($files)
  echo $file
  convert ./frame.png $file -geometry +10+50 -composite screenshots_with_frame/de/$file:r:t.png
  convert screenshots_with_frame/de/$file:r:t.png -resize 50% -colors 255 screenshots_with_frame/de/$file:r:t.png 
end

set files = `ls screenshots/en/*.png`
foreach file ($files)
  echo $file
  convert ./frame.png $file -geometry +10+50 -composite screenshots_with_frame/en/$file:r:t.png
  convert screenshots_with_frame/en/$file:r:t.png -resize 50% -colors 255 screenshots_with_frame/en/$file:r:t.png 
end


