# Requirements
## Functional
### Editor fragment
<img alt="editor fragment image" src="/docs/assets/editor_fragment.png" style="width: 76mm;">

- The application needs to start with an empty document after the start up 
- Display a section where an user can see a `.md` file
- Display a section where an user can edit a `.md` file
- There should a button to save the document
- During the save procedure if something go wrong open a toast massage
- There should a button to preview the document (if the library dose not support live preview)
- Show the name of the file
- When clicking on the preview text open the keyboard
- Above the keyboard there should be a button list insert markdown syntax
- The keyboard must be the system one
- There must be a button to open a file drawer

-------------------------------------------------------------------------------

### Slide fragment
<img alt="slide menu fragment image" src="/docs/assets/slide_menu_fragment.png" style="width: 76mm;">

- There should be a current directory overview with file and folder
- There should be a button to search in a sorted tree like index a file

-------------------------------------------------------------------------------

### File explorer fragment
<img alt="file explorer fragment image" src="/docs/assets/file_explorer_fragment.png" style="width: 76mm;">


-------------------------------------------------------------------------------

### Settings fragment
<img alt="settings fragment image" src="/docs/assets/settings_fragment.png" style="width: 76mm;">

- Should be a setting to change font and size of the text in the editor fragment
- Must be a menu where the user can set up a connection to a server 

-------------------------------------------------------------------------------

## Non functional
- Open only `.md` files
- The only Markdown syntax accepted is the one supported by one of this library:
  - [[tree sitter library]](https://github.com/MDeiml/tree-sitter-markdown)
  - [[Markwon]](https://github.com/noties/Markwon)
- Compatible with Android 9 API level 28 and above
- All the error message shown to the user must be specific and informative for the error that occurred
- The application should support the following file server:
  - Samba
  - SFTP
  - NextCloud
- The application can be connected to only one server
- The connection to a server must be persistent during the time
- The user can disconnect to a server in anytime
