echo "==== Build Type ===="
echo "1. Debug"
echo "2. Release"

while :
do
  read -p "Select the build type:" BUILD_TYPE
  case $BUILD_TYPE in
	1)
		echo "Debug it is!!"
    BUILD=debug
    break
		;;
	2)
		echo "Release it is!!"
    BUILD=release
		break
		;;
	*)
		echo "Enter proper choice!."
    exit 0;
		;;
  esac
done


echo "==== Flavour Options ===="
echo "0. NONE"
echo "1. CLOUDWALKER"
echo "2. CVTE"
echo "3. VIDEOTEX"
echo "4. WESTON"
echo "5. MEPL"
echo "6. GENERIC"
echo "7. AISEN"
echo "8. TOPTECH"
echo "9. DIXON"





while :
do
  read -p "Select the build you want to install:" FLAVOUR_TYPE
  case $FLAVOUR_TYPE in
  0)
    echo "Default !!"
    FLAVOUR=""
    break
    ;;
  1)
    echo "Cloudwalker it is!!"
    FLAVOUR=cloudwalker
    break
    ;;
  2)
    echo "Cvte it is!!"
    FLAVOUR=cvte
    break
    ;;
  3)
    echo "Videotex it is!!"
    FLAVOUR=videotex
    break
    ;;
  4)
    echo "Weston it is!!"
    FLAVOUR=weston
    break
    ;;
  5)
    echo "Mepl it is!!"
    FLAVOUR=mepl
    break
    ;;
  6)
    echo "Generic it is!!"
    FLAVOUR=generic
    break
    ;;
  7)
    echo "Aisen it is!!"
    FLAVOUR=aisen
    break
    ;;
  8)
    echo "Toptech !!"
    FLAVOUR=toptech
    break
    ;;
  9)
    echo "Dixon !!"
    FLAVOUR=dixon
    break
    ;;
  *)
    echo "Sorry, I don't understand enter proper choice next time."
    exit 0;
    ;;
  esac
done








echo "==== Select mBoard ===="
echo "1. 338/638/5510/358"
echo "2. 553"
echo "3. 708D"


while :
do
  read -p "Select the board you want to sign:" BOARD_TYPE
  case $BOARD_TYPE in
  1)
    echo "338/638/5510/358 it is !"
    SIGNPATH="$HOME/cloudwalker_signs/cv_338_638_5510_358"
    break
    ;;
  2)
    echo "553 it is!"
    SIGNPATH="$HOME/cloudwalker_signs/cv_553"
    break
     ;;
  3)
    echo "708D it is!"
    SIGNPATH="$HOME/cloudwalker_signs/cv_708D"
    break
     ;;
   *)
     echo "Proper board name not selected!"
     exit 0;
     ;;
   esac
 done

APP=$PWD
RELPATH=$APP/app/build/outputs/apk/$BUILD
rm -fv $RELPATH/*.apk
$APP/gradlew -p $APP/ clean
$APP/gradlew -p $APP/ assemble$BUILD
CAPK=`ls -t $RELPATH/*.apk | head -n1` 
APK=$(basename "$CAPK")
bash $SIGNPATH/sign.sh $RELPATH/$APK

FILENAME=${APK%.*}
EXTENSION=${APK##*.}


echo "==== Select Installation TYpe ===="
echo "1. copy to sdcard"
echo "2. install signed"
echo "3. install unsigned"

say "Select Installation Type"

while :
do
  #read -p "Select the install type:" INSTALL_SEL
  INSTALL_SEL=2
  case $INSTALL_SEL in
  1)
    echo "copying to sdcard.."
    adb push "$RELPATH/$FILENAME"_signed".$EXTENSION" /sdcard/
    break
    ;;
  2)
    echo "signed apk installing.."
    adb install -d -r -f "$RELPATH/$FILENAME"_signed".$EXTENSION"
    break
     ;;
  3)
    echo "unsigned apk installing.."
    adb install -d -r -f "$RELPATH/$FILENAME.$EXTENSION"
    break
     ;;
   *)
     echo "Proper option not selected!"
     exit 0;
     ;;
   esac
 done

afplay ~/Music/ding.mp3
