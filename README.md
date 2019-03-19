MaskedEditText
=========================

Extension of EditText that enables masking user input.
<br>Define a mask as shown in the example below and the non user input characters will be added automatically.

Example
------- 
``` 
User input:       123456789
Mask:             +(###) ###-###
Mask character:   #
_________________________________
Output:           +(123) 456-789
```


Usage
-----
Include `MaskedEditText` in your layout XML.
```
<mk.webfactory.dz.maskededittext.MaskedEditText
    android:id="@+id/edit_masked_input"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/sample_mask"
    android:inputType="text"
    android:minWidth="200dp"
    app:maskededittext_enforceMaskLength="false"
    app:maskededittext_mask="###-###-###"
    app:maskededittext_maskCharacter="#"
    />
```
You can also set the mask programatically. 
<br>If `enforceMaskLength` is set to true the length of the text does not exceed the length of the mask. 
<br>You can extract the raw user input (without the mask) by calling `MaskedEditText.getRawInput(): String`

*See Sample for more details*

Download
--------

_** For projects that do not use AndroidX use version `1.0`_

Gradle
```
implementation 'mk.webfactory.dz:maskededittext:2.0'
```
Maven
```
<dependency>
  <groupId>mk.webfactory.dz</groupId>
  <artifactId>maskededittext</artifactId>
  <version>2.0</version>
  <type>pom</type>
</dependency>
```

Limitations
-----------
- **Length input filters**
<br>The masking of user input, although correct, doesn't play well with `InputFilter.LengthFilter(maxLength)` or in XML `android:maxLength="10"`. As a recommendation, use `maskededittext_enforceMaskLength` only.

- **Some Input types**
<br>Some input types such as `textPassword` or `textCapCharacters/Sentences/Words` will be ignored by the MaskedEditText. Others, like numbers, work well though.

- **Additional TextWatchers**
<br>This component heavily relies on a `TextWatcher` to keep the input masked at all times. Adding your own should not cause any problems, but it does - under circumstances, the last change/callback is not reported leaving you poorly updated with the second to last change. <br>*Test this carefully if you want to be updated on the user input real time. See `R.id.txt_raw_input` in MainActivity of the Sample project for a not so great workaround.*

Acceptance criteria
-------------------
1. Mask is set in real time as input changes
2. Backspace removes masked characters only (raw user input)
3. Adding/removing text works anywhere in the current input
4. Enforce mask length trims excess text outside of mask. 
<br> 4.1. ~~Plays well with InputFilter.LengthFilter~~
5. ~~Input type is not respected only for non masked characters~~
6. Effectively separates raw input from masked input
7. Survives configuration changes automatically
8. Works on all android versions and devices
 
