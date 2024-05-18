package com.dothebestmayb.customview.presentation.ui.paint.model

data class VotingInfo(
    val drawingInfo: DrawingInfo,
    val votingStates: List<VotingState>,
) {
    fun checkMajority(): VotingResult {
        val majorityCount = votingStates.size / 2
        val numOfAccept = votingStates.count { it == VotingState.ACCEPT }
        if (numOfAccept > majorityCount) {
            return VotingResult.ACCEPT
        }
        val numOfDecline = votingStates.count { it == VotingState.DECLINE }
        if (numOfDecline > majorityCount) {
            return VotingResult.DECLINE
        }
        return VotingResult.YET
    }
}
