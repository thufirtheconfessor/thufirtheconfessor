## About

Thufir the Confessor is a barcode reader to receive files through a series of
2D barcodes. The programs to send files are easy to port and even short enough
to be typed in.

You can read more in the
[manual](http://thufirtheconfessor.github.io/thufirtheconfessor/manual.html).

## Installing

You must enable
[unknown sources](http://developer.android.com/tools/publishing/publishing_overview.html#unknown-sources)
to install Thufir. After that you can download and install the precompiled app
on the phone.

Alternatively if you have set up the Android SDK you can install the apk with

    adb install path/to/thufir-release.apk

You can test it on the
[canvas-ui](http://thufirtheconfessor.github.io/thufirtheconfessor/html5/canvas-ui.html)
sender.

## Compiling

1. Install the Android SDK
2. Extract the source archive
3. To initialize the project run at the top of the source tree

        android update project -n thufir -p .

4. To compile the program run

        ant debug

5. Install with

        adb install path/to/thufir-debub.apk

## License

Thufir the Confessor is licensed under the GPLv3.
See the [LICENSE](http://thufirtheconfessor.github.io/thufirtheconfessor/LICENSE.txt) file.
