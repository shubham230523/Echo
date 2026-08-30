package com.shubhamthorat.echo.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the hierarchical structure of a document detected during analysis.
 *
 * @property title The detected main title of the document.
 * @property headings A list of detected headings or sub-headings.
 * @property sections A list of identified content sections or chapters.
 */
@Serializable
data class DocumentStructure(
    val title: String,
    val headings: List<String>,
    val sections: List<String>
)
