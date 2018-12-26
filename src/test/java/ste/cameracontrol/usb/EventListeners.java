/*
 * cameracontrol
 * Copyright (C) 2010 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */
package ste.cameracontrol.usb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public abstract class EventListeners<T extends EventListener> {

    /**
     * The list with registered listeners.
     */
    private final List<T> listeners = Collections
            .synchronizedList(new ArrayList<T>());

    /**
     * Adds a listener.
     *
     * @param listener the listener to add.
     */
    public final void add(final T listener) {
        if (this.listeners.contains(listener)) {
            return;
        }
        this.listeners.add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener the listener to remove.
     */
    public final void remove(final T listener) {
        this.listeners.remove(listener);
    }

    /**
     * Removes all registered listeners.
     */
    public final void clear() {
        this.listeners.clear();
    }

    /**
     * Returns an array with the currently registered listeners. The returned
     * array is detached from the internal list of registered listeners.
     *
     * @return array with registered listeners.
     */
    public abstract T[] toArray();

    /**
     * Returns the listeners list.
     *
     * @return The listeners list.
     */
    protected final List<T> getListeners() {
        return this.listeners;
    }
}
