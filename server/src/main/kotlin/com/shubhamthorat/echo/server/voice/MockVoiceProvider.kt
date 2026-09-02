package com.shubhamthorat.echo.server.voice

class MockVoiceProvider : VoiceProvider {
    private val sampleUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

    override suspend fun getAvailableVoices(): List<BackendVoice> {
        return listOf(
            BackendVoice("flux-alexis-en", "Alexis (Warm)", "DEEPGRAM", "en-US", "FEMALE", sampleUrl),
            BackendVoice("flux-bree-en", "Bree (Clear)", "DEEPGRAM", "en-US", "FEMALE", sampleUrl),
            BackendVoice("flux-brittany-en", "Brittany (Bright)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-brooke-en", "Brooke (Soft)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-bruce-en", "Bruce (Deep)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-cliff-en", "Cliff (Narrator)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-cole-en", "Cole (Energetic)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-colin-en", "Colin (Professional)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-conor-en", "Conor (Friendly)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-donovan-en", "Donovan (Smooth)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-drew-en", "Drew (Classic)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-elise-en", "Elise (Gentle)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-gemma-en", "Gemma (Lively)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-haley-en", "Haley (Youthful)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-hannah-en", "Hannah (Polished)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-heather-en", "Heather (Calm)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-jack-en", "Jack (Casual)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-kit-en", "Kit (Neutral)", "DEEPGRAM", "en-US", "NEUTRAL", null),
            BackendVoice("flux-kai-en", "Kai (Balanced)", "DEEPGRAM", "en-US", "NEUTRAL", null),
            BackendVoice("flux-kelsey-en", "Kelsey (Upbeat)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-maeve-en", "Maeve (Authoritative)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-marcelo-en", "Marcelo (Charming)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-marcus-en", "Marcus (Strong)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-meena-en", "Meena (Expressive)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-meghan-en", "Meghan (Storyteller)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-miles-en", "Miles (Relaxed)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-paige-en", "Paige (Modern)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-priya-en", "Priya (Serene)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-rufus-en", "Rufus (Rich)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-sean-en", "Sean (Crisp)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-sharon-en", "Sharon (Mature)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-sienna-en", "Sienna (Soft-spoken)", "DEEPGRAM", "en-US", "FEMALE", null),
            BackendVoice("flux-tanner-en", "Tanner (Bold)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-wade-en", "Wade (Steady)", "DEEPGRAM", "en-US", "MALE", null),
            BackendVoice("flux-wes-en", "Wes (Resonant)", "DEEPGRAM", "en-US", "MALE", null)
        )
    }
}
