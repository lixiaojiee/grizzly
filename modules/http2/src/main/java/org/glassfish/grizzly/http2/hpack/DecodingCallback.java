/*
 * Copyright (c) 2016, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.grizzly.http2.hpack;

import org.glassfish.grizzly.Buffer;


/**
 * Delivers results of the {@link Decoder#decode(Buffer, boolean,
 * DecodingCallback) decoding operation}.
 *
 * <p> Methods of the callback are never called by a decoder with any of the
 * arguments being {@code null}.
 *
 * <p> The callback provides methods for all possible <a
 * href="https://tools.ietf.org/html/rfc7541#section-6">binary
 * representations</a>. This could be useful for implementing an intermediary,
 * logging, debugging, etc.
 *
 * <p> The callback is an interface in order to interoperate with lambdas (in
 * the most common use case):
 * <pre>{@code
 *     DecodingCallback callback = (name, value) -> System.out.println(name + ", " + value);
 * }</pre>
 *
 * <p> Names and values are {@link CharSequence}s rather than {@link String}s in
 * order to allow users to decide whether or not they need to create objects. A
 * {@code CharSequence} might be used in-place, for example, to be appended to
 * an {@link Appendable} (e.g. {@link StringBuilder}) and then discarded.
 *
 * <p> That said, if a passed {@code CharSequence} needs to outlast the method
 * call, it needs to be copied.
 */
@SuppressWarnings("UnusedParameters")
public abstract class DecodingCallback {

    /**
     * A method the more specific methods of the callback forward their calls
     * to.
     *
     * @param name
     *         header name
     * @param value
     *         header value
     */
    public abstract void onDecoded(CharSequence name, CharSequence value);

    /**
     * A more finer-grained version of {@link #onDecoded(CharSequence,
     * CharSequence)} that also reports on value sensitivity.
     *
     * <p> Value sensitivity must be considered, for example, when implementing
     * an intermediary. A {@code value} is sensitive if it was represented as <a
     * href="https://tools.ietf.org/html/rfc7541#section-6.2.3">Literal Header
     * Field Never Indexed</a>.
     *
     * <p> It is required that intermediaries MUST use the {@linkplain
     * Encoder#header(CharSequence, CharSequence, boolean) same representation}
     * for encoding this header field in order to protect its value which is not
     * to be put at risk by compressing it.
     *
     * <p> The default implementation invokes {@code onDecoded(name, value)}.
     *
     * @param name
     *         header name
     * @param value
     *         header value
     * @param sensitive
     *         whether or not the value is sensitive
     *
     * @see #onLiteralNeverIndexed(int, CharSequence, CharSequence, boolean)
     * @see #onLiteralNeverIndexed(CharSequence, boolean, CharSequence, boolean)
     */
    public void onDecoded(CharSequence name, CharSequence value,
                           boolean sensitive) {
        onDecoded(name, value);
    }

    /**
     * An <a href="https://tools.ietf.org/html/rfc7541#section-6.1">Indexed
     * Header Field</a> decoded.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, false)}.
     *
     * @param index
     *         index of an entry in the table
     * @param name
     *         header name
     * @param value
     *         header value
     */
    public void onIndexed(int index, CharSequence name, CharSequence value) {
        onDecoded(name, value, false);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.2">Literal
     * Header Field without Indexing</a> decoded, where a {@code name} was
     * referred by an {@code index}.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, false)}.
     *
     * @param index
     *         index of an entry in the table
     * @param name
     *         header name
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteral(int index, CharSequence name,
                           CharSequence value, boolean valueHuffman) {
        onDecoded(name, value, false);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.2">Literal
     * Header Field without Indexing</a> decoded, where both a {@code name} and
     * a {@code value} were literal.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, false)}.
     *
     * @param name
     *         header name
     * @param nameHuffman
     *         if the {@code name} was Huffman encoded
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteral(CharSequence name, boolean nameHuffman,
                           CharSequence value, boolean valueHuffman) {
        onDecoded(name, value, false);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.3">Literal
     * Header Field Never Indexed</a> decoded, where a {@code name}
     * was referred by an {@code index}.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, true)}.
     *
     * @param index
     *         index of an entry in the table
     * @param name
     *         header name
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteralNeverIndexed(int index, CharSequence name,
                                       CharSequence value,
                                       boolean valueHuffman) {
        onDecoded(name, value, true);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.3">Literal
     * Header Field Never Indexed</a> decoded, where both a {@code
     * name} and a {@code value} were literal.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, true)}.
     *
     * @param name
     *         header name
     * @param nameHuffman
     *         if the {@code name} was Huffman encoded
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteralNeverIndexed(CharSequence name, boolean nameHuffman,
                                       CharSequence value, boolean valueHuffman) {
        onDecoded(name, value, true);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.1">Literal
     * Header Field with Incremental Indexing</a> decoded, where a {@code name}
     * was referred by an {@code index}.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, false)}.
     *
     * @param index
     *         index of an entry in the table
     * @param name
     *         header name
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteralWithIndexing(int index,
                                       CharSequence name,
                                       CharSequence value, boolean valueHuffman) {
        onDecoded(name, value, false);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.2.1">Literal
     * Header Field with Incremental Indexing</a> decoded, where both a {@code
     * name} and a {@code value} were literal.
     *
     * <p> The default implementation invokes
     * {@code onDecoded(name, value, false)}.
     *
     * @param name
     *         header name
     * @param nameHuffman
     *         if the {@code name} was Huffman encoded
     * @param value
     *         header value
     * @param valueHuffman
     *         if the {@code value} was Huffman encoded
     */
    public void onLiteralWithIndexing(CharSequence name, boolean nameHuffman,
                                       CharSequence value, boolean valueHuffman) {
        onDecoded(name, value, false);
    }

    /**
     * A <a href="https://tools.ietf.org/html/rfc7541#section-6.3">Dynamic Table
     * Size Update</a> decoded.
     *
     * <p> The default implementation does nothing.
     *
     * @param capacity
     *         new capacity of the header table
     */
    public void onSizeUpdate(int capacity) { }
}
