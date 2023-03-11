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
- There must be a button to open a file explorer

-------------------------------------------------------------------------------

### Slide fragment
<img alt="slide menu fragment image" src="/docs/assets/slide_menu_fragment.png" style="width: 76mm;">

- There should be a current directory overview with files and folders
- There should be a button to search a local file in a ranked list

-------------------------------------------------------------------------------

### Connections fragment
<img alt="connections fragment image" src="/docs/assets/connections_fragment.png" style="width: 76mm;">

- The user can select one server
- The user can fill a form to connect to a specific server (ip and port)

-------------------------------------------------------------------------------

### File explorer fragment
<img alt="file explorer fragment image" src="/docs/assets/file_explorer_fragment.png" style="width: 76mm;">

- Should be displayed a file explorer of the server that the user has set up
- The title of the fragment is the name of the server that the user has chosen

-------------------------------------------------------------------------------

### Fonts fragment
<img alt="fonts fragment image" src="/docs/assets/fonts_fragment.png" style="width: 76mm;">

- The user can change fonts properties

-------------------------------------------------------------------------------

## Non functional
- Open only `.md` files
- The only Markdown syntax accepted is the one supported by one of this library:
  - [[Tree sitter library]](https://github.com/MDeiml/tree-sitter-markdown)
  - [[Markwon]](https://github.com/noties/Markwon)
- Compatible with Android 9 API level 28 and above
- All the error message shown to the user must be specific and informative for the error that occurred
- The application should support the following file server:
  - Samba
  - FTP
- The application can be connected to only one server
- The connection to a server must be persistent
- The user can disconnect from a server at anytime
- Use a pre-trained neural network (*2vec) to look for similar keywords
- Use the score produced by the neural network to index the most similar documents
- 
## Note
- [Samba library](https://github.com/hierynomus/smbj)