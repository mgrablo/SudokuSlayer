<div align="center">
  <a href="https://github.com/mgrablo/SudokuSlayer">
    <img src="./assets/logo.png" alt="SudokuSlayer Logo" width="80" height="80">
  </a>
  <br>
  <h1 align="center">SudokuSlayer</h1>
</div>


<p align="center">
  <img src="https://img.shields.io/github/license/mgrablo/SudokuSlayer?style=flat-square" alt="License" />
  <img src="https://img.shields.io/badge/Kotlin-2.2.20-purple?logo=kotlin&style=flat-square" alt="Kotlin" />
  <img src="https://img.shields.io/badge/platform-Android-green?logo=android&style=flat-square" alt="Platform" />
</p>

<p align="center">
Feature-rich Sudoku app for Android, designed for both beginners and experienced players.
</p>

<div align="center">
<a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/mgrablo/sudokuslayer">
	<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/main/assets/graphics/badge_obtainium.png" alt="Get it on Obtainium" align="center" height="54" />
</a>
</div>

<div align="center">
	<strong>SHA-256 hash of the signing certificate:</strong></br>
	<code> 5B:04:C6:C7:45:6C:9A:63:44:81:04:1B:ED:18:F8:3E:C2:E9:07:22:FA:12:96:A3:A2:45:A1:55:80:6D:6F:E1 </code>
</div>

<details>
    <summary>Table of Contents</summary>
    <ol>
        <li><a href="#features">Features</a></li>
        <li><a href="#planned-features">Planned Features</a></li>
        <li><a href="#screenshots">Screenshots</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#license">License</a></li>
        <li><a href="#acknowledgements">Acknowledgements</a></li>
    </ol>
</details>

## Features

- **Multiple Difficulties:** Play Sudoku on four different difficulty levels: easy, medium, hard, and expert.
- **Various Grid Sizes:** Choose from three grid sizes: 4x4, 9x9, and 16x16.
- **Hint System:** Get a little help when you're stuck. The hint system will first highlight the cells you should focus on, and if that's not enough, it will guide you toward the solution.
- **Statistics:** Track your progress with the insights screen, which shows your game statistics.
- **Themes:** Customize the look of the app with four different themes: Catppuccin Latte, Frappe, Mocha, and Macchiato.
- **Material 3 Expressive:** Uses Material 3 Expressive components for a modern, dynamic UI.
- **Customization:** Tailor the app to your preferences with options to:
  - Show or hide the timer.
  - Highlight rows, columns, matching numbers, or numbers that break Sudoku rules.
  - Automatically clear notes when inputting a number.

## Planned Features

- **More Sudoku Types:** Support for Killer Sudoku, Thermo Sudoku, and more.
- **More Themes:** Additional themes to further customize the app.
- **Translations:** Support for multiple languages.
- **Challenges:** Daily Sudokus and other challenges to test your skills.

## Screenshots

<table align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td align="center"><img src="assets/screenshots/Slice%201.png" alt="SudokuSlayer Screenshot 1" width="300" /></td>
    <td align="center"><img src="assets/screenshots/Slice%202.png" alt="SudokuSlayer Screenshot 2" width="300" /></td>
  </tr>
  <tr>
    <td align="center"><img src="assets/screenshots/Slice%203.png" alt="SudokuSlayer Screenshot 3" width="300" /></td>
    <td align="center"><img src="assets/screenshots/Slice%204.png" alt="SudokuSlayer Screenshot 4" width="300" /></td>
  </tr>
  <tr>
    <td align="center"><img src="assets/screenshots/Slice%205.png" alt="SudokuSlayer Screenshot 5" width="300" /></td>
    <td align="center"><img src="assets/screenshots/Slice%206.png" alt="SudokuSlayer Screenshot 6" width="300" /></td>
  </tr>
  <tr>
    <td align="center"><img src="assets/screenshots/Slice%207.png" alt="SudokuSlayer Screenshot 7" width="300" /></td>
    <td align="center"><img src="assets/screenshots/Slice%208.png" alt="SudokuSlayer Screenshot 8" width="300" /></td>
  </tr>
</table>

## Installation

<table>
  <tr>
    <td><strong>Method</strong></td>
    <td><strong>Link</strong></td>
  </tr>
  <tr>
    <td><strong>Direct Download</strong></td>
    <td><a href="https://github.com/mgrablo/sudokuslayer/releases/latest">Latest Release</a></td>
  </tr>
  <tr>
    <td><strong>Obtainium</strong></td>
    <td><a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/mgrablo/sudokuslayer"><img src="https://raw.githubusercontent.com/ImranR98/Obtainium/main/assets/graphics/badge_obtainium.png" alt="Get it on Obtainium" height="54" /></a></td>
  </tr>
</table>

### Signature Verification
SHA-256 hash of the signing certificate:

`5B:04:C6:C7:45:6C:9A:63:44:81:04:1B:ED:18:F8:3E:C2:E9:07:22:FA:12:96:A3:A2:45:A1:55:80:6D:6F:E1`

### Build from source
To build the app from source:

```bash
git clone https://github.com/mgrablo/SudokuSlayer
cd SudokuSlayer
./gradlew assembleRelease
```

## Contributing

Contributions are welcome! If you want to contribute to SudokuSlayer, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them with a descriptive message.
4. Push your changes to your fork.
5. Create a pull request to the main repository.

Feel free to open an [issue](https://github.com/mgrablo/SudokuSlayer/issues) for bugs or feature requests.

## License

This project is licensed under the GPL-3.0 License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

This app is built with the help of several open-source libraries and resources. Thank you to all the developers and contributors who made this project possible.

### Libraries

SudokuSlayer uses the following major open-source libraries:

- **Jetpack Compose:** For building the user interface.
- **Kotlin Coroutines:** For asynchronous programming.
- **Koin:** For dependency injection.
- **SQLDelight:** For local database storage.
- **[ConfettiKit](https://github.com/vinceglb/ConfettiKit):** For the fun confetti animations.
- **[Reorderable](https://github.com/Calvin-LL/Reorderable):** For reorderable lists in Compose.
- **[compose-unstyled](https://github.com/composablehorizons/compose-unstyled):** For building custom UI components.
- And many other great libraries! A full list can be found in the project's Gradle files.

### Themes

- The color palettes are based on the **[Catppuccin](https://catppuccin.com/)** theme.

### Algorithms

- The Sudoku solver is based on the DLX (Dancing Links) algorithm, created by Donald Knuth.
