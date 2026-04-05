# TriNova Quiz 🎮

Welcome to the **TriNova Quiz**! An ultra-fast, single-screen comedy trivia game. Instead of just basic right or wrong answers, TriNova provides instant gratification and savage humor.

Built with ❤️ by Team **TriNova**.

## Features 🚀
- **Instant Flow**: No delays or loading wait times. Just tap to skip and keep the momentum up.
- **Savage Wrong Answers (Roast Mode)** 😢: Guess wrong and you're hit with a disappointed dog and a customized, randomized roast!
- **Hype Train (Correct Answers)** 🎉: Get the answer right, and receive a hype message along with a legendary Dad Joke! Built-in streak trackers reward you the more you get correct.
- **Cat Rating System** 🐱: Finish all 10 questions and use our custom slider to rate the quiz. Depending on your score and rating, the app retrieves the perfect reacting Cat image to judge you properly.

## APIs Used 🌐
To make the quiz dynamic and hilarious, TriNova brings together a mashup of four completely different public APIs:
1. **[Open Trivia DB](https://opentdb.com/api_config.php)**: Feeds the app 10 random multiple-choice trivia questions per round.
2. **[Dog CEO API](https://dog.ceo/dog-api/)**: Fetches dog images used to shame the user for wrong answers.
3. **[icanhazdadjoke](https://icanhazdadjoke.com/)**: Fetches top-tier Dad Jokes as a reward for correct answers.
4. **[The Cat API](https://thecatapi.com/)**: Serves varying cat categories (from grumpy cats in "boxes" to happy cats with "sunglasses") to match the user's post-game slider rating.

## Tech Stack & Libraries 📚
TriNova is built on modern Android development standards:
- **Kotlin & Coroutines**: For fast, background thread API requests.
- **Retrofit2 & Gson**: To handle and serialize/deserialize all API network calls.
- **Coil**: For lightweight, seamless image loading of our dogs and cats (complete with crossfade animations).
- **StateFlow & ViewModels (MVVM)**: Uses a fully lifecycle-aware State Machine that cleanly drives every single UI transition.
- **Material3**: Powers the modern UI controls like the interactive Rating Slider (`com.google.android.material.slider.Slider`) and the rounded option buttons.

## Video Demo 🎥
Check out the TriNova Quiz in action!

*(Add your video demo link below)*
[**Link to TriNova Video Demo**](YOUR_VIDEO_LINK_HERE)
