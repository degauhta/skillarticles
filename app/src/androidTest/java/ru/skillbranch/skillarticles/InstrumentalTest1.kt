package ru.skillbranch.skillarticles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy

import org.mockito.Mockito.verify
import ru.skillbranch.skillarticles.markdown.spans.BlockquotesSpan
import ru.skillbranch.skillarticles.markdown.spans.HeaderSpan
import ru.skillbranch.skillarticles.markdown.spans.HorizontalRuleSpan
import ru.skillbranch.skillarticles.markdown.spans.IconLinkSpan
import ru.skillbranch.skillarticles.markdown.spans.InlineCodeSpan
import ru.skillbranch.skillarticles.markdown.spans.UnorderedListSpan

@RunWith(AndroidJUnit4::class)
class InstrumentalTest1 {

    @Test
    fun draw_list_item() {
        // settings
        val color = Color.RED
        val gap = 24f
        val radius = 8f

        // default
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location
        val ltop = 0
        val lbase = 60
        val lbottom = 80

        //mocks
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = UnorderedListSpan(gap, radius, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check leading margin
        assertEquals((4 * radius + gap).toInt(), span.getLeadingMargin(true))

        // check bullet draw
        span.drawLeadingMargin(canvas, paint, cml, 1, ltop, lbase, lbottom, text, 0, text.length, true, layout)

        // check order call
        val inOrder = inOrder(paint, canvas)
        // check first - set color to paint
        inOrder.verify(paint).color = color
        // check second - draw circle bullet
        inOrder.verify(canvas).drawCircle(gap + cml + radius, (lbottom - ltop) / 2f + ltop, radius, paint)
        // check paint color restore
        inOrder.verify(paint).color = defaultColor
    }

    @Test
    fun draw_quote() {
        // settings
        val color = Color.RED
        val gap = 24f
        val lineWidth = 8f

        // default
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location
        val ltop = 0
        val lbase = 60
        val lbottom = 80

        //mocks
        val canvas = mock(Canvas::class.java)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = BlockquotesSpan(gap, lineWidth, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check leading margin
        assertEquals((lineWidth + gap).toInt(), span.getLeadingMargin(true))

        // check line draw
        span.drawLeadingMargin(canvas, paint, cml, 1, ltop, lbase, lbottom, text, 0, text.length, true, layout)

        // check order call
        val inOrder = inOrder(paint, canvas)
        // check first - set color to paint
        inOrder.verify(paint).color = color
        inOrder.verify(paint).strokeWidth = lineWidth
        // check second - draw line
        inOrder.verify(canvas).drawLine(lineWidth / 2, ltop.toFloat(), lineWidth / 2f, lbottom.toFloat(), paint)
        // check paint color restore
        inOrder.verify(paint).color = defaultColor
    }

    @Test
    fun draw_header() {
        // settings
        val level = 1
        val textColor = Color.RED
        val lineColor = Color.GREEN
        val marginTop = 24f
        val marginBottom = 8f

        // default
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location
        val ltop = 0
        val lbase = 60
        val lbottom = 80

        //mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val measurePaint = mock(TextPaint::class.java)
        val drawPaint = mock(TextPaint::class.java)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = HeaderSpan(level, textColor, lineColor, marginTop, marginBottom)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check leading margin
        assertEquals(0, span.getLeadingMargin(true))

        // check measure state
        span.updateDrawState(measurePaint)
        verify(measurePaint).textSize *= span.sizes[level]!!
        verify(measurePaint).isFakeBoldText = true

        // check draw state
        span.updateDrawState(drawPaint)
        verify(drawPaint).textSize *= span.sizes[level]!!
        verify(drawPaint).isFakeBoldText = true
        verify(drawPaint).color = textColor

        // check line draw
        span.drawLeadingMargin(canvas, paint, cml, 1, ltop, lbase, lbottom, text, 0, text.length, true, layout)

        // check order call
        val inOrder = inOrder(paint, canvas)

        inOrder.verify(paint).color = lineColor

        val lh = (paint.descent() - paint.ascent()) * span.sizes[level]!!
        val lineOffset = lbase + lh * span.linePadding
        inOrder.verify(canvas).drawLine(0f, lineOffset, canvasWidth.toFloat(), lineOffset, paint)

        inOrder.verify(paint).color = defaultColor
    }

    @Test
    fun draw_rule() {
        // settings
        val color = Color.RED
        val width = 24f

        // default
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location
        val ltop = 0
        val lbase = 60
        val lbottom = 80

        //mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        val layout = mock(Layout::class.java)

        val text = SpannableString("text")

        val span = HorizontalRuleSpan(width, color)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check draw rule line
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint, canvas)

        inOrder.verify(paint).color = color

        inOrder.verify(canvas).drawLine(0f, (ltop + lbottom) / 2f, canvasWidth.toFloat(), (ltop + lbottom) / 2f, paint)
    }

    @Test
    fun draw_inline_code() {
        // settings
        val textColor = Color.RED
        val bgColor = Color.GREEN
        val cornerRadius = 8f
        val padding = 8f

        // default
        val canvasWidth = 700
        val measureText = 100f
        val defaultColor = Color.GRAY
        val cml = 0 // current margin location
        val ltop = 0
        val lbase = 60
        val lbottom = 80

        //mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(paint.measureText(anyString(), anyInt(), anyInt())).thenReturn(measureText)
        val layout = mock(Layout::class.java)
        val fm = mock(Paint.FontMetricsInt::class.java)

        val text = SpannableString("text")

        val span = InlineCodeSpan(textColor, bgColor, cornerRadius, padding)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // check measure size
        val size = span.getSize(paint, text, 0, text.length, fm)
        assertEquals((2 * padding + measureText).toInt(), size)

        // check draw inline code
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint, canvas)

        // check draw background
        inOrder.verify(paint).color = bgColor
        inOrder.verify(canvas).drawRoundRect(
            RectF(0f, ltop.toFloat(), measureText + 2 * padding, lbottom.toFloat()),
            cornerRadius,
            cornerRadius,
            paint
        )

        // check draw text
        inOrder.verify(paint).color = textColor
        inOrder.verify(canvas).drawText(text, 0, text.length, cml + padding, lbase.toFloat(), paint)
        inOrder.verify(paint).color = defaultColor
    }

    @Test
    fun draw_link() {
        //settings
        val iconColor: Int = Color.RED
        val padding: Float = 8f
        val textColor: Int = Color.BLUE

        //defaults
        val canvasWidth = 700
        val defaultColor = Color.GRAY
        val measureText = 100f
        val defaultAscent = -30
        val defaultDescent = 10
        val cml = 0 //current margin location
        val ltop = 0 //line top
        val lbase = 60 //line baseline
        val lbottom = 80 //line bottom

        //mocks
        val canvas = mock(Canvas::class.java)
        `when`(canvas.width).thenReturn(canvasWidth)
        val paint = mock(Paint::class.java)
        `when`(paint.color).thenReturn(defaultColor)
        `when`(
            paint.measureText(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(measureText)
        val fm = mock(Paint.FontMetricsInt::class.java)
        fm.ascent = defaultAscent
        fm.descent = defaultDescent

        //spy
        val linkDrawable: Drawable = spy(VectorDrawable())
        val path: Path = spy(Path())

        val text = SpannableString("text")

        val span = IconLinkSpan(linkDrawable, iconColor, padding, textColor)
        text.setSpan(span, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.path = path

        //check measure size
        val size = span.getSize(paint, text, 0, text.length, fm)
        assertEquals((defaultDescent - defaultAscent + padding + measureText).toInt(), size)

        //check drawable set bounds and set tint
        verify(linkDrawable).setBounds(0, 0, fm.descent - fm.ascent, fm.descent - fm.ascent)
        verify(linkDrawable).setTint(iconColor)

        //check draw icon and text
        span.draw(canvas, text, 0, text.length, cml.toFloat(), ltop, lbase, lbottom, paint)

        val inOrder = inOrder(paint, canvas, path, linkDrawable)

        //check path effect
        verify(paint, Mockito.atLeastOnce()).pathEffect = Mockito.any()
        verify(paint, Mockito.atLeastOnce()).strokeWidth = 0f
        inOrder.verify(paint).color = textColor

        //check reset path
        inOrder.verify(path).reset() //check reset before draw
        verify(path).moveTo(cml + span.iconSize + padding, lbottom.toFloat())
        verify(path).lineTo(cml + span.iconSize + padding + span.textWidth, lbottom.toFloat())

        //check draw path
        inOrder.verify(canvas).drawPath(path, paint)

        //check draw icon
        inOrder.verify(canvas).save()
        inOrder.verify(canvas).translate(
            cml.toFloat(),
            (lbottom - linkDrawable.bounds.bottom).toFloat()
        )
        inOrder.verify(linkDrawable).draw(canvas)
        inOrder.verify(canvas).restore()

        //check draw text
        inOrder.verify(paint).color = textColor
        inOrder.verify(canvas).drawText(
            text,
            0,
            text.length,
            cml + span.iconSize + padding,
            lbase.toFloat(),
            paint
        )
        inOrder.verify(paint).color = defaultColor
    }
}