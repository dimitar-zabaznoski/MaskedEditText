MaskedEditText
=========================

<table>
  <tr>
    <td>User input:</td>
    <td>123456789</td>
  </tr>
  <tr>
    <td>Mask:</td>
    <td>+(###) ###-###</td>
  </tr>
  <tr>
    <td>Mask character:</td>
    <td>#</td>
  </tr>
 <tr>
    <td>Output:</td>
    <td>+(123) 456-789</td>
  </tr>
</table>

### Acceptance criteria:
 1. Mask is set in real time as input changes
 2. Backspace removes masked characters only (raw user input)
 3. Adding/removing text works anywhere in the current input
 4. Input type is not respected only for non masked characters
 5. Enforce mask length trims excess text outside of mask. Overrides InputFilter.LengthFilter.
    Doesn't work with: TextCapCharacters, todo ...
 6. Effectively separates raw input from masked input
 7. Survives configuration changes automatically
 8. Persists state even if Activity is recreated
 9. Works on all android versions and devices
