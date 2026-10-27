package com.thryvii.app.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.thryvii.app.data.model.EntryType
import com.thryvii.app.data.model.StrokeData
import com.thryvii.app.data.model.StrokePoint
import com.thryvii.app.ui.drawableFor
import com.thryvii.app.ui.keyFor
import com.thryvii.app.data.model.JournalEntry as JournalEntryData

// The journal screens work with Compose types (Offset, Color, Dp) and drawable ids. Saved data
// uses plain numbers and string keys instead, so it can be written to disk. These two functions
// convert between the two shapes.

internal fun JournalEntryData.toUi(): JournalEntry = JournalEntry(
    title = title,
    caption = caption,
    pageNumber = pageNumber,
    preview = preview,
    type = if (type == EntryType.DRAWING) EntryEditorMode.DRAWING else EntryEditorMode.WRITING,
    date = date,
    prompt = prompt,
    fullBody = fullBody,
    photoRes = photoKey?.let { drawableFor(it) },
    artRes = artKey?.let { drawableFor(it) },
    viewerReady = viewerReady,
    strokes = strokes?.map { stroke ->
        DrawnStroke(
            points = stroke.points.map { Offset(it.x, it.y) },
            color = Color(stroke.colorArgb),
            strokeWidthDp = stroke.widthDp.dp
        )
    }
)

internal fun JournalEntry.toData(): JournalEntryData = JournalEntryData(
    title = title,
    caption = caption,
    pageNumber = pageNumber,
    preview = preview,
    type = if (type == EntryEditorMode.DRAWING) EntryType.DRAWING else EntryType.WRITING,
    date = date,
    prompt = prompt,
    fullBody = fullBody,
    photoKey = keyFor(photoRes),
    artKey = keyFor(artRes),
    viewerReady = viewerReady,
    strokes = strokes?.map { stroke ->
        StrokeData(
            points = stroke.points.map { StrokePoint(it.x, it.y) },
            colorArgb = stroke.color.toArgb(),
            widthDp = stroke.strokeWidthDp.value
        )
    }
)
